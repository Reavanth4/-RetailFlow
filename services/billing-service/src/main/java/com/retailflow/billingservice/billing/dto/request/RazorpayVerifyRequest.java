package com.retailflow.billingservice.billing.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RazorpayVerifyRequest(
        @NotBlank String razorpayOrderId,
        @NotBlank String razorpayPaymentId,
        @NotBlank String razorpaySignature) {
}
