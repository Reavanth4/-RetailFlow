package com.retailflow.salesservice.sale.service.imp;

import com.retailflow.salesservice.client.InventoryGateway;
import com.retailflow.salesservice.common.exception.InsufficientStockException;
import com.retailflow.salesservice.common.exception.InvalidRequestException;
import com.retailflow.salesservice.sale.dto.request.ReturnCreateRequest;
import com.retailflow.salesservice.sale.dto.request.ReturnItemRequest;
import com.retailflow.salesservice.sale.dto.request.SaleCreateRequest;
import com.retailflow.salesservice.sale.dto.request.SaleItemRequest;
import com.retailflow.salesservice.sale.entity.Sale;
import com.retailflow.salesservice.sale.entity.SaleItem;
import com.retailflow.salesservice.sale.entity.SaleStatus;
import com.retailflow.salesservice.sale.mapper.ReturnMapper;
import com.retailflow.salesservice.sale.mapper.SaleMapper;
import com.retailflow.salesservice.sale.repository.SaleRepository;
import com.retailflow.salesservice.sale.repository.SaleReturnRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleReturnRepository saleReturnRepository;

    @Mock
    private SaleMapper saleMapper;

    @Mock
    private ReturnMapper returnMapper;

    @Mock
    private InventoryGateway inventoryGateway;

    private SaleServiceImpl saleService;

    @BeforeEach
    void setUp() {
        saleService = new SaleServiceImpl(
                saleRepository, saleReturnRepository, saleMapper, returnMapper, inventoryGateway);
    }

    private Sale buildSale(SaleStatus status) {
        Sale sale = new Sale();
        sale.setCustomerId(1L);
        sale.setWarehouseId(1L);
        sale.setSaleNumber("SL-000001");
        sale.setSubtotal(new BigDecimal("10000.00"));
        sale.setTotal(new BigDecimal("10000.00"));
        sale.setStatus(status);

        SaleItem item = SaleItem.builder()
                .productId(1L)
                .quantity(2)
                .unitPrice(new BigDecimal("5000.00"))
                .total(new BigDecimal("10000.00"))
                .build();
        sale.addItem(item);

        return sale;
    }

    @Test
    void completeSale_shouldDeductStockAndComplete() {
        Sale sale = buildSale(SaleStatus.DRAFT);
        sale.setId(1L);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(inventoryGateway.getAvailableStock(1L, 1L)).thenReturn(10);

        saleService.completeSale(1L);

        assertThat(sale.getStatus()).isEqualTo(SaleStatus.COMPLETED);
        verify(inventoryGateway).stockOut(eq(1L), eq(1L), eq(2), eq("SALE"), eq("SALE"), eq(1L), any());
    }

    @Test
    void completeSale_shouldThrow_whenInsufficientStock() {
        Sale sale = buildSale(SaleStatus.DRAFT);
        sale.setId(1L);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(inventoryGateway.getAvailableStock(1L, 1L)).thenReturn(1);

        assertThatThrownBy(() -> saleService.completeSale(1L))
                .isInstanceOf(InsufficientStockException.class);
        verify(inventoryGateway, never()).stockOut(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void completeSale_shouldThrow_whenNotDraft() {
        Sale sale = buildSale(SaleStatus.COMPLETED);
        sale.setId(1L);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        assertThatThrownBy(() -> saleService.completeSale(1L))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void createReturn_shouldRestockAndRecordReturn() {
        Sale sale = buildSale(SaleStatus.COMPLETED);
        sale.setId(1L);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleReturnRepository.findBySaleId(1L)).thenReturn(List.of());
        when(saleReturnRepository.save(any(com.retailflow.salesservice.sale.entity.SaleReturn.class)))
                .thenAnswer(invocation -> {
                    com.retailflow.salesservice.sale.entity.SaleReturn r =
                            invocation.getArgument(0);
                    r.setId(5L);
                    return r;
                });

        ReturnItemRequest itemRequest = new ReturnItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(1);

        ReturnCreateRequest request = new ReturnCreateRequest();
        request.setReason("Damaged");
        request.setItems(List.of(itemRequest));

        saleService.createReturn(1L, request);

        verify(inventoryGateway).stockIn(eq(1L), eq(1L), eq(1), eq("RETURN_IN"), eq("RETURN"), eq(5L), any());
    }

    @Test
    void createReturn_shouldThrow_whenQuantityExceedsEligible() {
        Sale sale = buildSale(SaleStatus.COMPLETED);
        sale.setId(1L);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleReturnRepository.findBySaleId(1L)).thenReturn(List.of());

        ReturnItemRequest itemRequest = new ReturnItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(5);

        ReturnCreateRequest request = new ReturnCreateRequest();
        request.setItems(List.of(itemRequest));

        assertThatThrownBy(() -> saleService.createReturn(1L, request))
                .isInstanceOf(InvalidRequestException.class);
        verify(inventoryGateway, never()).stockIn(any(), any(), any(), any(), any(), any(), any());
    }
}
