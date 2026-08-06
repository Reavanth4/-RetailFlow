package com.retailflow.productservice.product.repository;

import com.retailflow.productservice.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findByBarcode(String barcode);

    boolean existsBySku(String sku);

    boolean existsByBarcode(String barcode);

    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("select p from Product p where p.active = true and "
            + "(lower(p.name) like %:search% or lower(p.sku) like %:search% "
            + "or lower(p.barcode) like %:search%)")
    Page<Product> search(@Param("search") String search, Pageable pageable);

}