package com.retailflow.billingservice.billing.service;

import com.retailflow.billingservice.billing.dto.request.RazorpayVerifyRequest;
import com.retailflow.billingservice.billing.dto.response.PaymentResponse;
import com.retailflow.billingservice.billing.entity.Bill;
import com.retailflow.billingservice.billing.entity.Payment;
import com.retailflow.billingservice.billing.entity.PaymentMethod;
import com.retailflow.billingservice.billing.entity.PaymentStatus;
import com.retailflow.billingservice.billing.entity.RazorpayOrder;
import com.retailflow.billingservice.billing.entity.RazorpayOrderStatus;
import com.retailflow.billingservice.billing.mapper.PaymentMapper;
import com.retailflow.billingservice.billing.repository.BillRepository;
import com.retailflow.billingservice.billing.repository.PaymentRepository;
import com.retailflow.billingservice.billing.repository.RazorpayOrderRepository;
import com.retailflow.billingservice.common.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RazorpayPaymentServiceTest {
    private static final String SECRET = "test-secret";

    @Mock private BillRepository billRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private RazorpayOrderRepository orderRepository;
    @Mock private PaymentMapper paymentMapper;

    private RazorpayPaymentService service;

    @BeforeEach
    void setUp() {
        service = new RazorpayPaymentService(
                billRepository, paymentRepository, orderRepository, paymentMapper, "rzp_test_key", SECRET);
    }

    @Test
    void verifiedSignaturePersistsSuccessfulRazorpayPaymentAndMarksBillPaid() throws Exception {
        String orderId = "order_123";
        String paymentId = "pay_456";
        RazorpayOrder order = new RazorpayOrder(10L, orderId, new BigDecimal("499.00"));
        Bill bill = Bill.builder().saleId(20L).total(new BigDecimal("499.00"))
                .paymentStatus(PaymentStatus.PENDING).build();
        bill.setId(10L);
        PaymentResponse mapped = new PaymentResponse();

        when(orderRepository.findByProviderOrderId(orderId)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByTransactionReference(paymentId)).thenReturn(false);
        when(billRepository.findById(10L)).thenReturn(Optional.of(bill));
        when(paymentRepository.findByBillIdAndStatus(any(), any()))
                .thenReturn(List.of(Payment.builder().amount(new BigDecimal("499.00")).build()));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(mapped);

        PaymentResponse result = service.verify(new RazorpayVerifyRequest(
                orderId, paymentId, signature(orderId, paymentId)));

        ArgumentCaptor<Payment> saved = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(saved.capture());
        assertThat(saved.getValue().getPaymentMethod()).isEqualTo(PaymentMethod.RAZORPAY);
        assertThat(saved.getValue().getTransactionReference()).isEqualTo(paymentId);
        assertThat(order.getStatus()).isEqualTo(RazorpayOrderStatus.PAID);
        assertThat(bill.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(result).isSameAs(mapped);
    }

    @Test
    void invalidSignatureCannotCreatePayment() {
        RazorpayOrder order = new RazorpayOrder(10L, "order_123", new BigDecimal("499.00"));
        when(orderRepository.findByProviderOrderId("order_123")).thenReturn(Optional.of(order));
        when(paymentRepository.existsByTransactionReference("pay_456")).thenReturn(false);

        assertThatThrownBy(() -> service.verify(new RazorpayVerifyRequest("order_123", "pay_456", "00")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("signature is invalid");
        verify(paymentRepository, never()).save(any());
        verify(billRepository, never()).save(any());
    }

    private String signature(String orderId, String paymentId) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal((orderId + "|" + paymentId)
                .getBytes(StandardCharsets.UTF_8)));
    }
}
