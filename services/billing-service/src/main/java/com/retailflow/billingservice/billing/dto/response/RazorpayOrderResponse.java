package com.retailflow.billingservice.billing.dto.response;

public record RazorpayOrderResponse(String keyId, String orderId, long amount, String currency,
                                    String invoiceNumber) {
}
