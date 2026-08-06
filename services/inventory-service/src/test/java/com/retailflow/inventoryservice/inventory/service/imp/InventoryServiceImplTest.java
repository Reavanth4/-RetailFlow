package com.retailflow.inventoryservice.inventory.service.imp;

import com.retailflow.inventoryservice.common.exception.InsufficientStockException;
import com.retailflow.inventoryservice.common.exception.InvalidRequestException;
import com.retailflow.inventoryservice.inventory.dto.request.AdjustStockRequest;
import com.retailflow.inventoryservice.inventory.dto.request.StockInRequest;
import com.retailflow.inventoryservice.inventory.dto.request.StockOutRequest;
import com.retailflow.inventoryservice.inventory.dto.request.TransferRequest;
import com.retailflow.inventoryservice.inventory.entity.AdjustmentType;
import com.retailflow.inventoryservice.inventory.entity.Inventory;
import com.retailflow.inventoryservice.inventory.entity.MovementType;
import com.retailflow.inventoryservice.inventory.mapper.InventoryMapper;
import com.retailflow.inventoryservice.inventory.repository.InventoryRepository;
import com.retailflow.inventoryservice.inventory.service.StockMovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StockMovementService stockMovementService;

    @Mock
    private InventoryMapper inventoryMapper;

    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl(inventoryRepository, stockMovementService, inventoryMapper);
    }

    private Inventory inventory(int quantity, int reserved) {
        Inventory i = new Inventory();
        i.setProductId(1L);
        i.setWarehouseId(1L);
        i.setQuantity(quantity);
        i.setReservedQuantity(reserved);
        i.setActive(true);
        return i;
    }

    @Test
    void stockIn_shouldIncreaseQuantity() {
        Inventory inventory = inventory(100, 0);
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockInRequest request = new StockInRequest();
        request.setProductId(1L);
        request.setWarehouseId(1L);
        request.setQuantity(20);
        request.setMovementType(MovementType.PURCHASE);

        inventoryService.stockIn(request);

        assertThat(inventory.getQuantity()).isEqualTo(120);
        verify(stockMovementService).recordMovement(
                eq(1L), eq(1L), eq(MovementType.PURCHASE), eq(20), any(), any(), any());
    }

    @Test
    void stockOut_shouldDecreaseQuantity() {
        Inventory inventory = inventory(100, 0);
        when(inventoryRepository.findByProductIdAndWarehouseIdForUpdate(1L, 1L))
                .thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockOutRequest request = new StockOutRequest();
        request.setProductId(1L);
        request.setWarehouseId(1L);
        request.setQuantity(5);
        request.setMovementType(MovementType.SALE);

        inventoryService.stockOut(request);

        assertThat(inventory.getQuantity()).isEqualTo(95);
        verify(stockMovementService).recordMovement(
                eq(1L), eq(1L), eq(MovementType.SALE), eq(-5), any(), any(), any());
    }

    @Test
    void stockOut_shouldThrow_whenInsufficientStock() {
        Inventory inventory = inventory(2, 0);
        when(inventoryRepository.findByProductIdAndWarehouseIdForUpdate(1L, 1L))
                .thenReturn(Optional.of(inventory));

        StockOutRequest request = new StockOutRequest();
        request.setProductId(1L);
        request.setWarehouseId(1L);
        request.setQuantity(5);
        request.setMovementType(MovementType.SALE);

        assertThatThrownBy(() -> inventoryService.stockOut(request))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void transfer_shouldMoveStockBetweenWarehouses() {
        Inventory source = inventory(100, 0);
        source.setWarehouseId(1L);
        Inventory destination = inventory(0, 0);
        destination.setWarehouseId(2L);

        when(inventoryRepository.findByProductIdAndWarehouseIdForUpdate(1L, 1L))
                .thenReturn(Optional.of(source));
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 2L))
                .thenReturn(Optional.of(destination));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(stockMovementService.recordMovement(any(), any(), any(), any(), any(), any(), any()))
                .thenAnswer(invocation -> {
                    com.retailflow.inventoryservice.inventory.entity.StockMovement sm =
                            new com.retailflow.inventoryservice.inventory.entity.StockMovement();
                    sm.setId(10L);
                    return sm;
                });

        TransferRequest request = new TransferRequest();
        request.setProductId(1L);
        request.setSourceWarehouseId(1L);
        request.setDestinationWarehouseId(2L);
        request.setQuantity(20);

        inventoryService.transfer(request);

        assertThat(source.getQuantity()).isEqualTo(80);
        assertThat(destination.getQuantity()).isEqualTo(20);
        verify(stockMovementService).recordMovement(
                eq(1L), eq(1L), eq(MovementType.TRANSFER_OUT), eq(-20), any(), any(), any());
        verify(stockMovementService).recordMovement(
                eq(1L), eq(2L), eq(MovementType.TRANSFER_IN), eq(20), any(), any(), any());
    }

    @Test
    void transfer_shouldThrow_whenSameWarehouse() {
        TransferRequest request = new TransferRequest();
        request.setProductId(1L);
        request.setSourceWarehouseId(1L);
        request.setDestinationWarehouseId(1L);
        request.setQuantity(20);

        assertThatThrownBy(() -> inventoryService.transfer(request))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void adjustStock_shouldIncreaseQuantity_whenAdjustmentIn() {
        Inventory inventory = inventory(100, 0);
        when(inventoryRepository.findByProductIdAndWarehouseIdForUpdate(1L, 1L))
                .thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdjustStockRequest request = new AdjustStockRequest();
        request.setProductId(1L);
        request.setWarehouseId(1L);
        request.setQuantity(5);
        request.setAdjustmentType(AdjustmentType.IN);

        inventoryService.adjustStock(request);

        assertThat(inventory.getQuantity()).isEqualTo(105);
        verify(stockMovementService).recordMovement(
                eq(1L), eq(1L), eq(MovementType.ADJUSTMENT_IN), eq(5), any(), any(), any());
    }

    @Test
    void adjustStock_shouldDecreaseQuantity_whenAdjustmentOut() {
        Inventory inventory = inventory(100, 0);
        when(inventoryRepository.findByProductIdAndWarehouseIdForUpdate(1L, 1L))
                .thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdjustStockRequest request = new AdjustStockRequest();
        request.setProductId(1L);
        request.setWarehouseId(1L);
        request.setQuantity(10);
        request.setAdjustmentType(AdjustmentType.OUT);

        inventoryService.adjustStock(request);

        assertThat(inventory.getQuantity()).isEqualTo(90);
        verify(stockMovementService).recordMovement(
                eq(1L), eq(1L), eq(MovementType.ADJUSTMENT_OUT), eq(-10), any(), any(), any());
    }

    @Test
    void adjustStock_shouldThrow_whenAdjustmentOutExceedsStock() {
        Inventory inventory = inventory(3, 0);
        when(inventoryRepository.findByProductIdAndWarehouseIdForUpdate(1L, 1L))
                .thenReturn(Optional.of(inventory));

        AdjustStockRequest request = new AdjustStockRequest();
        request.setProductId(1L);
        request.setWarehouseId(1L);
        request.setQuantity(10);
        request.setAdjustmentType(AdjustmentType.OUT);

        assertThatThrownBy(() -> inventoryService.adjustStock(request))
                .isInstanceOf(InsufficientStockException.class);
    }
}
