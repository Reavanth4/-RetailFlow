package com.retailflow.inventoryservice.inventory.service.imp;

import com.retailflow.inventoryservice.common.dto.PageResponse;
import com.retailflow.inventoryservice.common.exception.InsufficientStockException;
import com.retailflow.inventoryservice.common.exception.InvalidRequestException;
import com.retailflow.inventoryservice.common.exception.ResourceNotFoundException;
import com.retailflow.inventoryservice.inventory.dto.request.AdjustStockRequest;
import com.retailflow.inventoryservice.inventory.dto.request.StockInRequest;
import com.retailflow.inventoryservice.inventory.dto.request.StockOutRequest;
import com.retailflow.inventoryservice.inventory.dto.request.TransferRequest;
import com.retailflow.inventoryservice.inventory.dto.response.InventoryResponse;
import com.retailflow.inventoryservice.inventory.dto.response.TransferResponse;
import com.retailflow.inventoryservice.inventory.entity.AdjustmentType;
import com.retailflow.inventoryservice.inventory.entity.Inventory;
import com.retailflow.inventoryservice.inventory.entity.MovementType;
import com.retailflow.inventoryservice.inventory.entity.StockMovement;
import com.retailflow.inventoryservice.inventory.mapper.InventoryMapper;
import com.retailflow.inventoryservice.inventory.repository.InventoryRepository;
import com.retailflow.inventoryservice.inventory.service.InventoryService;
import com.retailflow.inventoryservice.inventory.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    private final StockMovementService stockMovementService;

    private final InventoryMapper inventoryMapper;

    @Override
    public InventoryResponse getInventoryById(Long inventoryId) {
        return inventoryMapper.toResponse(findInventory(inventoryId));
    }

    @Override
    public PageResponse<InventoryResponse> getAllInventories(Long warehouseId, Pageable pageable) {
        org.springframework.data.domain.Page<Inventory> page =
                warehouseId == null
                        ? inventoryRepository.findByActiveTrue(pageable)
                        : inventoryRepository.findByActiveTrueAndWarehouseId(warehouseId, pageable);

        List<InventoryResponse> content = page.getContent().stream()
                .map(inventoryMapper::toResponse)
                .toList();

        return PageResponse.from(page, content);
    }

    @Override
    public List<InventoryResponse> getInventoryByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId).stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    public List<InventoryResponse> getInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    public InventoryResponse getInventoryByProductAndWarehouse(Long productId, Long warehouseId) {
        return inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .map(inventoryMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory found for product " + productId
                                + " in warehouse " + warehouseId));
    }

    @Override
    public List<InventoryResponse> getLowStock(int threshold) {
        return inventoryRepository.findByActiveTrueAndQuantityLessThanEqual(threshold).stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    public InventoryResponse stockIn(StockInRequest request) {
        Inventory inventory = getOrCreateInventory(request.getProductId(), request.getWarehouseId());

        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        inventory = inventoryRepository.save(inventory);

        stockMovementService.recordMovement(
                request.getProductId(),
                request.getWarehouseId(),
                request.getMovementType(),
                request.getQuantity(),
                request.getReferenceType(),
                request.getReferenceId(),
                request.getRemarks());

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public InventoryResponse stockOut(StockOutRequest request) {
        Inventory inventory = findForUpdate(request.getProductId(), request.getWarehouseId());

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available: "
                            + inventory.getAvailableQuantity()
                            + ", requested: " + request.getQuantity());
        }

        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventory = inventoryRepository.save(inventory);

        stockMovementService.recordMovement(
                request.getProductId(),
                request.getWarehouseId(),
                request.getMovementType(),
                -request.getQuantity(),
                request.getReferenceType(),
                request.getReferenceId(),
                request.getRemarks());

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public TransferResponse transfer(TransferRequest request) {
        if (Objects.equals(request.getSourceWarehouseId(), request.getDestinationWarehouseId())) {
            throw new InvalidRequestException(
                    "Source and destination warehouse cannot be the same.");
        }

        Inventory source = findForUpdate(request.getProductId(), request.getSourceWarehouseId());

        if (source.getAvailableQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock in source warehouse "
                            + request.getSourceWarehouseId()
                            + ". Available: " + source.getAvailableQuantity()
                            + ", requested: " + request.getQuantity());
        }

        Inventory destination = getOrCreateInventory(
                request.getProductId(), request.getDestinationWarehouseId());

        source.setQuantity(source.getQuantity() - request.getQuantity());
        inventoryRepository.save(source);

        destination.setQuantity(destination.getQuantity() + request.getQuantity());
        inventoryRepository.save(destination);

        StockMovement transferOut = stockMovementService.recordMovement(
                request.getProductId(),
                request.getSourceWarehouseId(),
                MovementType.TRANSFER_OUT,
                -request.getQuantity(),
                "TRANSFER",
                null,
                request.getRemarks());

        StockMovement transferIn = stockMovementService.recordMovement(
                request.getProductId(),
                request.getDestinationWarehouseId(),
                MovementType.TRANSFER_IN,
                request.getQuantity(),
                "TRANSFER",
                null,
                request.getRemarks());

        return TransferResponse.builder()
                .productId(request.getProductId())
                .sourceWarehouseId(request.getSourceWarehouseId())
                .destinationWarehouseId(request.getDestinationWarehouseId())
                .quantity(request.getQuantity())
                .transferOutMovementId(transferOut.getId())
                .transferInMovementId(transferIn.getId())
                .message("Stock transferred successfully")
                .build();
    }

    @Override
    public InventoryResponse adjustStock(AdjustStockRequest request) {
        Inventory inventory = findForUpdate(request.getProductId(), request.getWarehouseId());

        MovementType movementType;
        if (request.getAdjustmentType() == AdjustmentType.IN) {
            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
            movementType = MovementType.ADJUSTMENT_IN;
        } else {
            if (inventory.getAvailableQuantity() < request.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for adjustment. Available: "
                                + inventory.getAvailableQuantity()
                                + ", requested: " + request.getQuantity());
            }
            inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
            movementType = MovementType.ADJUSTMENT_OUT;
        }

        inventory = inventoryRepository.save(inventory);

        stockMovementService.recordMovement(
                request.getProductId(),
                request.getWarehouseId(),
                movementType,
                request.getAdjustmentType() == AdjustmentType.IN
                        ? request.getQuantity() : -request.getQuantity(),
                "ADJUSTMENT",
                null,
                request.getReason());

        return inventoryMapper.toResponse(inventory);
    }

    private Inventory getOrCreateInventory(Long productId, Long warehouseId) {
        return inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseGet(() -> {
                    Inventory inventory = new Inventory();
                    inventory.setProductId(productId);
                    inventory.setWarehouseId(warehouseId);
                    inventory.setQuantity(0);
                    inventory.setReservedQuantity(0);
                    inventory.setActive(true);
                    return inventoryRepository.save(inventory);
                });
    }

    private Inventory findForUpdate(Long productId, Long warehouseId) {
        return inventoryRepository.findByProductIdAndWarehouseIdForUpdate(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory record for product " + productId
                                + " in warehouse " + warehouseId));
    }

    private Inventory findInventory(Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found with id: " + inventoryId));
    }
}
