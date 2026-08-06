package com.retailflow.salesservice.sale.repository;

import com.retailflow.salesservice.sale.entity.SaleReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleReturnRepository extends JpaRepository<SaleReturn, Long> {

    List<SaleReturn> findBySaleId(Long saleId);
}
