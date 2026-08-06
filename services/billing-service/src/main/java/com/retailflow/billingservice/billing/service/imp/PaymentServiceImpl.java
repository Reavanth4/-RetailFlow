package com.retailflow.billingservice.billing.service.imp;

import com.retailflow.billingservice.billing.dto.request.PaymentCreateRequest;
import com.retailflow.billingservice.billing.dto.response.PaymentResponse;
import com.retailflow.billingservice.billing.entity.Bill;
import com.retailflow.billingservice.billing.entity.Payment;
import com.retailflow.billingservice.billing.entity.PaymentMethod;
import com.retailflow.billingservice.billing.entity.PaymentStatus;
import com.retailflow.billingservice.billing.entity.TransactionStatus;
import com.retailflow.billingservice.billing.mapper.PaymentMapper;
import com.retailflow.billingservice.billing.repository.BillRepository;
import com.retailflow.billingservice.billing.repository.PaymentRepository;
import com.retailflow.billingservice.billing.service.PaymentService;
import com.retailflow.billingservice.common.exception.InvalidRequestException;
import com.retailflow.billingservice.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        Payment payment = paymentMapper.toEntity(request);
        payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        payment.setStatus(TransactionStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        updateBillPaymentStatus(request.getBillId());
        log.info("Payment of [{}] recorded for sale [{}]", payment.getAmount(), payment.getSaleId());

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentMapper.toResponseList(paymentRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsBySale(Long saleId) {
        return paymentMapper.toResponseList(paymentRepository.findBySaleId(saleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByBill(Long billId) {
        return paymentMapper.toResponseList(paymentRepository.findByBillId(billId));
    }

    private void updateBillPaymentStatus(Long billId) {
        if (billId == null) {
            return;
        }

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));

        BigDecimal totalPaid = paymentRepository
                .findByBillIdAndStatus(billId, TransactionStatus.SUCCESS)
                .stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(bill.getTotal()) >= 0) {
            bill.setPaymentStatus(PaymentStatus.PAID);
        } else {
            bill.setPaymentStatus(PaymentStatus.PARTIAL);
        }
        billRepository.save(bill);
    }
}
