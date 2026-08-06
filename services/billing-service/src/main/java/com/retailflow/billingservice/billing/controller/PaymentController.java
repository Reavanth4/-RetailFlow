package com.retailflow.billingservice.billing.controller;

import com.retailflow.billingservice.billing.dto.request.PaymentCreateRequest;
import com.retailflow.billingservice.billing.dto.response.PaymentResponse;
import com.retailflow.billingservice.billing.service.PaymentService;
import com.retailflow.billingservice.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentCreateRequest request) {

        return ApiResponse.success(
                "Payment recorded successfully",
                paymentService.createPayment(request)
        );
    }

    @GetMapping
    public ApiResponse<List<PaymentResponse>> getAllPayments() {

        return ApiResponse.success(
                "Payments fetched successfully",
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/sale/{saleId}")
    public ApiResponse<List<PaymentResponse>> getPaymentsBySale(
            @PathVariable Long saleId) {

        return ApiResponse.success(
                "Payments fetched successfully",
                paymentService.getPaymentsBySale(saleId)
        );
    }

    @GetMapping("/bill/{billId}")
    public ApiResponse<List<PaymentResponse>> getPaymentsByBill(
            @PathVariable Long billId) {

        return ApiResponse.success(
                "Payments fetched successfully",
                paymentService.getPaymentsByBill(billId)
        );
    }
}
