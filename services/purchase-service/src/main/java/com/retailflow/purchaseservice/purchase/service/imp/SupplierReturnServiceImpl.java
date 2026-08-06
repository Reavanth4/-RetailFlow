package com.retailflow.purchaseservice.purchase.service.imp;

import com.retailflow.purchaseservice.client.InventoryGateway;
import com.retailflow.purchaseservice.common.exception.InvalidRequestException;
import com.retailflow.purchaseservice.common.exception.ResourceNotFoundException;
import com.retailflow.purchaseservice.purchase.dto.request.SupplierReturnCreateRequest;
import com.retailflow.purchaseservice.purchase.dto.request.SupplierReturnItemRequest;
import com.retailflow.purchaseservice.purchase.dto.response.SupplierReturnResponse;
import com.retailflow.purchaseservice.purchase.entity.Purchase;
import com.retailflow.purchaseservice.purchase.entity.PurchaseItem;
import com.retailflow.purchaseservice.purchase.entity.PurchaseStatus;
import com.retailflow.purchaseservice.purchase.entity.SupplierReturn;
import com.retailflow.purchaseservice.purchase.entity.SupplierReturnItem;
import com.retailflow.purchaseservice.purchase.mapper.SupplierReturnMapper;
import com.retailflow.purchaseservice.purchase.repository.PurchaseRepository;
import com.retailflow.purchaseservice.purchase.repository.SupplierReturnRepository;
import com.retailflow.purchaseservice.purchase.service.SupplierReturnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SupplierReturnServiceImpl implements SupplierReturnService {

    private final SupplierReturnRepository supplierReturnRepository;

    private final PurchaseRepository purchaseRepository;

    private final SupplierReturnMapper supplierReturnMapper;

    private final InventoryGateway inventoryGateway;

    @Override
    public SupplierReturnResponse createSupplierReturn(Long purchaseId, SupplierReturnCreateRequest request) {
        Purchase purchase = findPurchase(purchaseId);

        if (purchase.getStatus() != PurchaseStatus.RECEIVED) {
            throw new InvalidRequestException(
                    "Returns are only allowed for received purchases, current status: "
                            + purchase.getStatus());
        }

        Map<Long, Integer> returnedByProduct = existingReturnedQuantities(purchaseId);
        Map<Long, Integer> requestedInThisReturn = new HashMap<>();

        SupplierReturn supplierReturn = supplierReturnMapper.toEntity(request);
        supplierReturn.setReturnNumber(generateReturnNumber());
        supplierReturn.setPurchaseId(purchaseId);
        supplierReturn.setSupplierId(purchase.getSupplierId());
        supplierReturn.setWarehouseId(purchase.getWarehouseId());
        supplierReturn.setReturnDate(request.getReturnDate() != null
                ? request.getReturnDate() : LocalDate.now());

        for (SupplierReturnItemRequest itemRequest : request.getItems()) {
            PurchaseItem purchaseItem = findPurchaseItem(purchase, itemRequest.getProductId());
            Integer alreadyReturned = returnedByProduct.getOrDefault(itemRequest.getProductId(), 0)
                    + requestedInThisReturn.getOrDefault(itemRequest.getProductId(), 0);
            Integer eligible = purchaseItem.getQuantity() - alreadyReturned;

            if (itemRequest.getQuantity() > eligible) {
                throw new InvalidRequestException(
                        "Return quantity " + itemRequest.getQuantity()
                                + " exceeds eligible quantity " + eligible
                                + " for product " + itemRequest.getProductId());
            }

            SupplierReturnItem item = supplierReturnMapper.toItemEntity(itemRequest);
            item.setUnitPrice(purchaseItem.getUnitPrice());
            supplierReturn.addItem(item);

            requestedInThisReturn.merge(itemRequest.getProductId(), itemRequest.getQuantity(), Integer::sum);
        }

        supplierReturn = supplierReturnRepository.save(supplierReturn);

        for (SupplierReturnItem item : supplierReturn.getItems()) {
            inventoryGateway.stockOut(
                    item.getProductId(),
                    purchase.getWarehouseId(),
                    item.getQuantity(),
                    "RETURN_OUT",
                    "SUPPLIER_RETURN",
                    supplierReturn.getId(),
                    "Returned to supplier for purchase " + purchase.getPurchaseNumber());
        }

        log.info("Supplier return [{}] created for purchase [{}]",
                supplierReturn.getReturnNumber(), purchase.getPurchaseNumber());

        return supplierReturnMapper.toResponse(supplierReturn);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierReturnResponse getSupplierReturnById(Long returnId) {
        return supplierReturnMapper.toResponse(supplierReturnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier return not found with id: " + returnId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierReturnResponse> getAllSupplierReturns() {
        return supplierReturnMapper.toResponseList(supplierReturnRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierReturnResponse> getReturnsByPurchase(Long purchaseId) {
        return supplierReturnMapper.toResponseList(
                supplierReturnRepository.findByPurchaseId(purchaseId));
    }

    private Map<Long, Integer> existingReturnedQuantities(Long purchaseId) {
        Map<Long, Integer> result = new HashMap<>();
        for (SupplierReturn existing : supplierReturnRepository.findByPurchaseId(purchaseId)) {
            for (SupplierReturnItem item : existing.getItems()) {
                result.merge(item.getProductId(), item.getQuantity(), Integer::sum);
            }
        }
        return result;
    }

    private PurchaseItem findPurchaseItem(Purchase purchase, Long productId) {
        return purchase.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException(
                        "Product " + productId + " was not part of this purchase"));
    }

    private Purchase findPurchase(Long purchaseId) {
        return purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase not found with id: " + purchaseId));
    }

    private String generateReturnNumber() {
        return "SR-" + String.format("%06d", supplierReturnRepository.count() + 1);
    }
}
