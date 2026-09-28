package com.retailflow.billingservice.billing.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.retailflow.billingservice.billing.dto.request.RazorpayVerifyRequest;
import com.retailflow.billingservice.billing.dto.response.PaymentResponse;
import com.retailflow.billingservice.billing.dto.response.RazorpayOrderResponse;
import com.retailflow.billingservice.billing.entity.Bill;
import com.retailflow.billingservice.billing.entity.Payment;
import com.retailflow.billingservice.billing.entity.PaymentMethod;
import com.retailflow.billingservice.billing.entity.PaymentStatus;
import com.retailflow.billingservice.billing.entity.RazorpayOrder;
import com.retailflow.billingservice.billing.entity.RazorpayOrderStatus;
import com.retailflow.billingservice.billing.entity.TransactionStatus;
import com.retailflow.billingservice.billing.mapper.PaymentMapper;
import com.retailflow.billingservice.billing.repository.BillRepository;
import com.retailflow.billingservice.billing.repository.PaymentRepository;
import com.retailflow.billingservice.billing.repository.RazorpayOrderRepository;
import com.retailflow.billingservice.common.exception.DuplicateResourceException;
import com.retailflow.billingservice.common.exception.InvalidRequestException;
import com.retailflow.billingservice.common.exception.ResourceNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class RazorpayPaymentService {
    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final RazorpayOrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final String keyId;
    private final String keySecret;

    public RazorpayPaymentService(BillRepository billRepository, PaymentRepository paymentRepository,
                                  RazorpayOrderRepository orderRepository, PaymentMapper paymentMapper,
                                  @Value("${razorpay.key-id:}") String keyId,
                                  @Value("${razorpay.key-secret:}") String keySecret) {
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentMapper = paymentMapper;
        this.keyId = keyId;
        this.keySecret = keySecret;
    }

    @Transactional
    public RazorpayOrderResponse createOrder(Long billId) {
        requireConfiguration();
        Bill bill = findBill(billId);
        BigDecimal outstanding = outstandingFor(bill);
        if (outstanding.signum() <= 0) {
            throw new InvalidRequestException("This bill has already been paid");
        }
        long paise = outstanding.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
        try {
            JSONObject options = new JSONObject();
            options.put("amount", paise);
            options.put("currency", "INR");
            options.put("receipt", bill.getInvoiceNumber());
            Order providerOrder = new RazorpayClient(keyId, keySecret).orders.create(options);
            String providerOrderId = providerOrder.get("id");
            orderRepository.save(new RazorpayOrder(billId, providerOrderId, outstanding));
            return new RazorpayOrderResponse(keyId, providerOrderId, paise, "INR", bill.getInvoiceNumber());
        } catch (Exception exception) {
            throw new InvalidRequestException("Unable to create Razorpay order: " + exception.getMessage());
        }
    }

    @Transactional
    public PaymentResponse verify(RazorpayVerifyRequest request) {
        requireConfiguration();
        RazorpayOrder order = orderRepository.findByProviderOrderId(request.razorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Unknown Razorpay order"));
        if (order.getStatus() == RazorpayOrderStatus.PAID
                || paymentRepository.existsByTransactionReference(request.razorpayPaymentId())) {
            throw new DuplicateResourceException("This payment has already been processed");
        }
        if (!validSignature(request)) {
            throw new InvalidRequestException("Razorpay payment signature is invalid");
        }

        Bill bill = findBill(order.getBillId());
        Payment payment = Payment.builder()
                .saleId(bill.getSaleId())
                .billId(bill.getId())
                .paymentMethod(PaymentMethod.RAZORPAY)
                .amount(order.getAmount())
                .status(TransactionStatus.SUCCESS)
                .transactionReference(request.razorpayPaymentId())
                .paidAt(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);
        order.markPaid();
        orderRepository.save(order);
        bill.setPaymentStatus(outstandingFor(bill).signum() <= 0 ? PaymentStatus.PAID : PaymentStatus.PARTIAL);
        billRepository.save(bill);
        return paymentMapper.toResponse(payment);
    }

    private boolean validSignature(RazorpayVerifyRequest request) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] expected = mac.doFinal((request.razorpayOrderId() + "|" + request.razorpayPaymentId())
                    .getBytes(StandardCharsets.UTF_8));
            byte[] supplied = HexFormat.of().parseHex(request.razorpaySignature());
            return MessageDigest.isEqual(expected, supplied);
        } catch (Exception exception) {
            return false;
        }
    }

    private BigDecimal outstandingFor(Bill bill) {
        BigDecimal paid = paymentRepository.findByBillIdAndStatus(bill.getId(), TransactionStatus.SUCCESS).stream()
                .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return bill.getTotal().subtract(paid);
    }

    private Bill findBill(Long billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));
    }

    private void requireConfiguration() {
        if (keyId.isBlank() || keySecret.isBlank()) {
            throw new InvalidRequestException("Razorpay is not configured");
        }
    }
}
