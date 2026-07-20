package com.retailflow.productservice.brand.repository;

import com.retailflow.productservice.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    /**
     * Find a brand by its name.
     */
    Optional<Brand> findByName(String name);

    /**
     * Find a brand by its unique brand code.
     */
    Optional<Brand> findByBrandCode(String brandCode);

    /**
     * Check whether a brand name already exists.
     */
    boolean existsByName(String name);

    /**
     * Check whether a brand code already exists.
     */
    boolean existsByBrandCode(String brandCode);

}