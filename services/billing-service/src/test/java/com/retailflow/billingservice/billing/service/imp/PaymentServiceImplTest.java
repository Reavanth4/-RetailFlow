package com.retailflow.billingservice.billing.service.imp;

import com.retailflow.billingservice.billing.dto.request.PaymentCreateRequest;
import com.retailflow.billingservice.billing.entity.Bill;
import com.retailflow.billingservice.billing.entity.Payment;
import com.retailflow.billingservice.billing.entity.PaymentStatus;
import com.retailflow.billingservice.billing.mapper.PaymentMapper;
import com.retailflow.billingservice.billing.repository.BillRepository;
import com.retailflow.billingservice.billing.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private PaymentMapper paymentMapper;

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository, billRepository, paymentMapper);
    }

    @Test
    void createPayment_shouldMarkBillPaid_whenFullAmount() {
        Bill bill = new Bill();
        bill.setId(1L);
        bill.setTotal(new BigDecimal("10000.00"));
        bill.setPaymentStatus(PaymentStatus.PENDING);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("10000.00"));

        when(paymentMapper.toEntity(any(PaymentCreateRequest.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));
        when(paymentRepository.findByBillIdAndStatus(1L, com.retailflow.billingservice.billing.entity.TransactionStatus.SUCCESS))
                .thenReturn(List.of(payment));

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setSaleId(1L);
        request.setBillId(1L);
        request.setPaymentMethod("CASH");
        request.setAmount(new BigDecimal("10000.00"));

        paymentService.createPayment(request);

        assertThat(bill.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
    }

    @Test
    void createPayment_shouldMarkBillPartial_whenPartialAmount() {
        Bill bill = new Bill();
        bill.setId(1L);
        bill.setTotal(new BigDecimal("10000.00"));
        bill.setPaymentStatus(PaymentStatus.PENDING);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("4000.00"));

        when(paymentMapper.toEntity(any(PaymentCreateRequest.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));
        when(paymentRepository.findByBillIdAndStatus(1L, com.retailflow.billingservice.billing.entity.TransactionStatus.SUCCESS))
                .thenReturn(List.of(payment));

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setSaleId(1L);
        request.setBillId(1L);
        request.setPaymentMethod("UPI");
        request.setAmount(new BigDecimal("4000.00"));

        paymentService.createPayment(request);

        assertThat(bill.getPaymentStatus()).isEqualTo(PaymentStatus.PARTIAL);
    }
}
