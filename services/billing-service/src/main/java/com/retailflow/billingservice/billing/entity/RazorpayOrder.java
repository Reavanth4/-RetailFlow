package com.retailflow.billingservice.billing.entity;

import com.retailflow.billingservice.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "razorpay_orders")
public class RazorpayOrder extends BaseEntity {
    @Column(nullable = false)
    private Long billId;
    @Column(nullable = false, unique = true, length = 100)
    private String providerOrderId;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RazorpayOrderStatus status;

    protected RazorpayOrder() { }

    public RazorpayOrder(Long billId, String providerOrderId, BigDecimal amount) {
        this.billId = billId;
        this.providerOrderId = providerOrderId;
        this.amount = amount;
        this.status = RazorpayOrderStatus.CREATED;
    }

    public Long getBillId() { return billId; }
    public String getProviderOrderId() { return providerOrderId; }
    public BigDecimal getAmount() { return amount; }
    public RazorpayOrderStatus getStatus() { return status; }
    public void markPaid() { this.status = RazorpayOrderStatus.PAID; }
}
