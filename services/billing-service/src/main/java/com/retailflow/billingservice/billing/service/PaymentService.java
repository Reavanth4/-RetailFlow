package com.retailflow.billingservice.billing.service;

import com.retailflow.billingservice.billing.dto.request.PaymentCreateRequest;
import com.retailflow.billingservice.billing.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentCreateRequest request);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsBySale(Long saleId);

    List<PaymentResponse> getPaymentsByBill(Long billId);
}
