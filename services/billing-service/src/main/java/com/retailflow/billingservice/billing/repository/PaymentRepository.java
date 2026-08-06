package com.retailflow.billingservice.billing.repository;

import com.retailflow.billingservice.billing.entity.Payment;
import com.retailflow.billingservice.billing.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySaleId(Long saleId);

    List<Payment> findByBillId(Long billId);

    List<Payment> findByBillIdAndStatus(Long billId, TransactionStatus status);
}
