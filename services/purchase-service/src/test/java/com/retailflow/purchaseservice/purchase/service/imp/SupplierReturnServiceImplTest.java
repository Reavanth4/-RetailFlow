package com.retailflow.purchaseservice.purchase.service.imp;

import com.retailflow.purchaseservice.client.InventoryGateway;
import com.retailflow.purchaseservice.common.exception.InvalidRequestException;
import com.retailflow.purchaseservice.purchase.dto.request.SupplierReturnCreateRequest;
import com.retailflow.purchaseservice.purchase.dto.request.SupplierReturnItemRequest;
import com.retailflow.purchaseservice.purchase.entity.Purchase;
import com.retailflow.purchaseservice.purchase.entity.PurchaseItem;
import com.retailflow.purchaseservice.purchase.entity.PurchaseStatus;
import com.retailflow.purchaseservice.purchase.entity.SupplierReturn;
import com.retailflow.purchaseservice.purchase.entity.SupplierReturnItem;
import com.retailflow.purchaseservice.purchase.mapper.SupplierReturnMapper;
import com.retailflow.purchaseservice.purchase.repository.PurchaseRepository;
import com.retailflow.purchaseservice.purchase.repository.SupplierReturnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierReturnServiceImplTest {

    @Mock
    private SupplierReturnRepository supplierReturnRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private SupplierReturnMapper supplierReturnMapper;

    @Mock
    private InventoryGateway inventoryGateway;

    private SupplierReturnServiceImpl supplierReturnService;

    @BeforeEach
    void setUp() {
        supplierReturnService = new SupplierReturnServiceImpl(
                supplierReturnRepository, purchaseRepository, supplierReturnMapper, inventoryGateway);
    }

    private Purchase buildReceivedPurchase(int quantity) {
        Purchase purchase = new Purchase();
        purchase.setId(1L);
        purchase.setSupplierId(10L);
        purchase.setWarehouseId(1L);
        purchase.setPurchaseNumber("PO-000001");
        purchase.setStatus(PurchaseStatus.RECEIVED);

        PurchaseItem item = PurchaseItem.builder()
                .productId(1L)
                .quantity(quantity)
                .unitPrice(new BigDecimal("4000.00"))
                .build();
        purchase.addItem(item);

        return purchase;
    }

    private SupplierReturnItemRequest itemRequest(int quantity) {
        SupplierReturnItemRequest request = new SupplierReturnItemRequest();
        request.setProductId(1L);
        request.setQuantity(quantity);
        request.setReason("Damaged goods");
        return request;
    }

    @Test
    void createSupplierReturn_shouldDeductStockForReceivedPurchase() {
        Purchase purchase = buildReceivedPurchase(100);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(supplierReturnRepository.findByPurchaseId(1L)).thenReturn(List.of());
        when(supplierReturnRepository.count()).thenReturn(0L);
        when(supplierReturnRepository.save(any(SupplierReturn.class))).thenAnswer(invocation -> {
            SupplierReturn saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });
        when(supplierReturnMapper.toEntity(any(SupplierReturnCreateRequest.class)))
                .thenReturn(new SupplierReturn());
        when(supplierReturnMapper.toItemEntity(any(SupplierReturnItemRequest.class)))
                .thenAnswer(invocation -> {
                    SupplierReturnItemRequest r = invocation.getArgument(0);
                    return SupplierReturnItem.builder()
                            .productId(r.getProductId())
                            .quantity(r.getQuantity())
                            .reason(r.getReason())
                            .build();
                });

        SupplierReturnCreateRequest request = new SupplierReturnCreateRequest();
        request.setItems(List.of(itemRequest(10)));

        supplierReturnService.createSupplierReturn(1L, request);

        ArgumentCaptor<SupplierReturn> captor = ArgumentCaptor.forClass(SupplierReturn.class);
        verify(supplierReturnRepository).save(captor.capture());
        SupplierReturn result = captor.getValue();

        assertThat(result.getReturnNumber()).isEqualTo("SR-000001");
        assertThat(result.getItems()).hasSize(1);
        verify(inventoryGateway).stockOut(eq(1L), eq(1L), eq(10), eq("RETURN_OUT"), eq("SUPPLIER_RETURN"), eq(5L), any());
    }

    @Test
    void createSupplierReturn_shouldThrow_whenNotReceived() {
        Purchase purchase = buildReceivedPurchase(100);
        purchase.setStatus(PurchaseStatus.ORDERED);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        SupplierReturnCreateRequest request = new SupplierReturnCreateRequest();
        request.setItems(List.of(itemRequest(10)));

        assertThatThrownBy(() -> supplierReturnService.createSupplierReturn(1L, request))
                .isInstanceOf(InvalidRequestException.class);
        verify(inventoryGateway, never()).stockOut(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void createSupplierReturn_shouldThrow_whenExceedsEligibleQuantity() {
        Purchase purchase = buildReceivedPurchase(10);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(supplierReturnRepository.findByPurchaseId(1L)).thenReturn(List.of());
        when(supplierReturnMapper.toEntity(any(SupplierReturnCreateRequest.class)))
                .thenReturn(new SupplierReturn());

        SupplierReturnCreateRequest request = new SupplierReturnCreateRequest();
        request.setItems(List.of(itemRequest(50)));

        assertThatThrownBy(() -> supplierReturnService.createSupplierReturn(1L, request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("eligible quantity");
        verify(inventoryGateway, never()).stockOut(any(), any(), any(), any(), any(), any(), any());
    }
}
