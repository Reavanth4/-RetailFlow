package com.retailflow.billingservice.billing.repository;

import com.retailflow.billingservice.billing.entity.RazorpayOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RazorpayOrderRepository extends JpaRepository<RazorpayOrder, Long> {
    Optional<RazorpayOrder> findByProviderOrderId(String providerOrderId);
}
