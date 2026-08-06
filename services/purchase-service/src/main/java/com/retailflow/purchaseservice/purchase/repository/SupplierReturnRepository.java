package com.retailflow.purchaseservice.purchase.repository;

import com.retailflow.purchaseservice.purchase.entity.SupplierReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierReturnRepository extends JpaRepository<SupplierReturn, Long> {

    List<SupplierReturn> findByPurchaseId(Long purchaseId);
}
