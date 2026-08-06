package com.retailflow.billingservice.billing.dto.response;

import com.retailflow.billingservice.billing.entity.PaymentMethod;
import com.retailflow.billingservice.billing.entity.TransactionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private Long id;
    private Long saleId;
    private Long billId;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private TransactionStatus status;
    private String transactionReference;
    private LocalDateTime paidAt;
}
