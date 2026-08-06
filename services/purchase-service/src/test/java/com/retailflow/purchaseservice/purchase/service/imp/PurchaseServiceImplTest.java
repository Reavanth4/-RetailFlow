package com.retailflow.purchaseservice.purchase.service.imp;

import com.retailflow.purchaseservice.client.InventoryGateway;
import com.retailflow.purchaseservice.common.exception.DuplicateResourceException;
import com.retailflow.purchaseservice.common.exception.InvalidRequestException;
import com.retailflow.purchaseservice.purchase.dto.request.PurchaseCreateRequest;
import com.retailflow.purchaseservice.purchase.dto.request.PurchaseItemRequest;
import com.retailflow.purchaseservice.purchase.dto.response.PurchaseResponse;
import com.retailflow.purchaseservice.purchase.entity.Purchase;
import com.retailflow.purchaseservice.purchase.entity.PurchaseStatus;
import com.retailflow.purchaseservice.purchase.mapper.PurchaseMapper;
import com.retailflow.purchaseservice.purchase.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class PurchaseServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private PurchaseMapper purchaseMapper;

    @Mock
    private InventoryGateway inventoryGateway;

    private PurchaseServiceImpl purchaseService;

    @BeforeEach
    void setUp() {
        purchaseService = new PurchaseServiceImpl(purchaseRepository, purchaseMapper, inventoryGateway);
    }

    private PurchaseCreateRequest createRequest() {
        PurchaseItemRequest item = new PurchaseItemRequest();
        item.setProductId(1L);
        item.setQuantity(100);
        item.setUnitPrice(new BigDecimal("40.00"));
        item.setTax(BigDecimal.ZERO);
        item.setDiscount(BigDecimal.ZERO);

        PurchaseCreateRequest request = new PurchaseCreateRequest();
        request.setSupplierId(1L);
        request.setWarehouseId(1L);
        request.setItems(List.of(item));
        return request;
    }

    @Test
    void createPurchase_shouldComputeTotalsAndStartAsDraft() {
        when(purchaseRepository.count()).thenReturn(0L);
        when(purchaseRepository.save(any(Purchase.class)))
                .thenAnswer(invocation -> {
                    Purchase p = invocation.getArgument(0);
                    p.setId(1L);
                    return p;
                });
        when(purchaseMapper.toResponse(any(Purchase.class))).thenAnswer(invocation -> {
            Purchase p = invocation.getArgument(0);
            PurchaseResponse response = new PurchaseResponse();
            response.setStatus(p.getStatus());
            response.setPurchaseNumber(p.getPurchaseNumber());
            response.setSubtotal(p.getSubtotal());
            response.setTotal(p.getTotal());
            return response;
        });

        PurchaseResponse response = purchaseService.createPurchase(createRequest());

        assertThat(response.getStatus()).isEqualTo(PurchaseStatus.DRAFT);
        assertThat(response.getPurchaseNumber()).isEqualTo("PO-000001");
        assertThat(response.getSubtotal()).isEqualByComparingTo("4000.00");
        assertThat(response.getTotal()).isEqualByComparingTo("4000.00");
    }

    @Test
    void receivePurchase_shouldCallInventoryAndSetReceived() {
        Purchase purchase = buildPurchase(PurchaseStatus.ORDERED);
        purchase.setId(1L);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(purchaseRepository.save(any(Purchase.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(inventoryGateway).stockIn(any(), any(), any(), any(), any(), any(), any());

        purchaseService.receivePurchase(1L);

        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.RECEIVED);
        verify(inventoryGateway).stockIn(
                eq(1L), eq(1L), eq(100), eq("PURCHASE"), eq("PURCHASE"), eq(1L), any());
    }

    @Test
    void receivePurchase_shouldThrow_whenAlreadyReceived() {
        Purchase purchase = buildPurchase(PurchaseStatus.RECEIVED);
        purchase.setId(1L);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> purchaseService.receivePurchase(1L))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void cancelPurchase_shouldThrow_whenReceived() {
        Purchase purchase = buildPurchase(PurchaseStatus.RECEIVED);
        purchase.setId(1L);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> purchaseService.cancelPurchase(1L))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void orderPurchase_shouldThrow_whenNotDraft() {
        Purchase purchase = buildPurchase(PurchaseStatus.RECEIVED);
        purchase.setId(1L);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> purchaseService.orderPurchase(1L))
                .isInstanceOf(InvalidRequestException.class);
    }

    private Purchase buildPurchase(PurchaseStatus status) {
        Purchase purchase = new Purchase();
        purchase.setSupplierId(1L);
        purchase.setWarehouseId(1L);
        purchase.setPurchaseNumber("PO-000001");
        purchase.setSubtotal(new BigDecimal("4000.00"));
        purchase.setTotal(new BigDecimal("4000.00"));
        purchase.setStatus(status);

        com.retailflow.purchaseservice.purchase.entity.PurchaseItem item =
                com.retailflow.purchaseservice.purchase.entity.PurchaseItem.builder()
                        .productId(1L)
                        .quantity(100)
                        .unitPrice(new BigDecimal("40.00"))
                        .total(new BigDecimal("4000.00"))
                        .build();
        purchase.addItem(item);

        return purchase;
    }
}
