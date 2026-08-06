package com.retailflow.billingservice.billing.service.imp;

import com.retailflow.billingservice.billing.dto.request.BillCreateRequest;
import com.retailflow.billingservice.billing.dto.request.BillItemRequest;
import com.retailflow.billingservice.billing.entity.Bill;
import com.retailflow.billingservice.billing.entity.PaymentStatus;
import com.retailflow.billingservice.billing.mapper.BillMapper;
import com.retailflow.billingservice.billing.printer.PrinterService;
import com.retailflow.billingservice.billing.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private BillMapper billMapper;

    @Mock
    private PrinterService printerService;

    private BillServiceImpl billService;

    @BeforeEach
    void setUp() {
        billService = new BillServiceImpl(billRepository, billMapper, printerService);
    }

    private BillCreateRequest buildRequest() {
        BillCreateRequest request = new BillCreateRequest();
        request.setSaleId(1L);
        request.setCustomerId(1L);
        request.setDiscount(new BigDecimal("100.00"));
        request.setTax(new BigDecimal("50.00"));

        BillItemRequest item = new BillItemRequest();
        item.setProductId(1L);
        item.setProductName("Gold Bangle");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("5000.00"));

        request.setItems(List.of(item));
        return request;
    }

    @Test
    void createBill_shouldComputeTotalsAndAssignInvoiceNumber() {
        BillCreateRequest request = buildRequest();

        Bill mapped = new Bill();
        mapped.setSaleId(request.getSaleId());
        mapped.setCustomerId(request.getCustomerId());
        mapped.setDiscount(request.getDiscount());
        mapped.setTax(request.getTax());

        BillItemRequest itemRequest = request.getItems().get(0);
        com.retailflow.billingservice.billing.entity.BillItem item =
                com.retailflow.billingservice.billing.entity.BillItem.builder()
                        .productId(itemRequest.getProductId())
                        .productName(itemRequest.getProductName())
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(itemRequest.getUnitPrice())
                        .build();

        when(billMapper.toEntity(any(BillCreateRequest.class))).thenReturn(mapped);
        when(billMapper.toItemEntity(any(BillItemRequest.class))).thenReturn(item);
        when(billRepository.count()).thenReturn(0L);
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        billService.createBill(request);

        ArgumentCaptor<Bill> captor = ArgumentCaptor.forClass(Bill.class);
        verify(billRepository).save(captor.capture());
        Bill created = captor.getValue();

        assertThat(created.getInvoiceNumber()).isEqualTo("INV-000001");
        assertThat(created.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(created.getSubtotal()).isEqualByComparingTo("10000.00");
        assertThat(created.getDiscount()).isEqualByComparingTo("100.00");
        assertThat(created.getTax()).isEqualByComparingTo("50.00");
        assertThat(created.getTotal()).isEqualByComparingTo("9950.00");
        assertThat(created.getItems()).hasSize(1);
    }

    @Test
    void printBill_shouldDelegateToPrinterService() {
        Bill bill = new Bill();
        bill.setId(1L);
        bill.setInvoiceNumber("INV-000001");

        when(billRepository.findById(1L)).thenReturn(java.util.Optional.of(bill));

        billService.printBill(1L);

        verify(printerService).print(any());
    }
}
