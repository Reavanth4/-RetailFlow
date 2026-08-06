package com.retailflow.productservice.product.repository;

import com.retailflow.productservice.brand.entity.Brand;
import com.retailflow.productservice.brand.repository.BrandRepository;
import com.retailflow.productservice.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    private Brand saveBrand() {
        Brand brand = new Brand();
        brand.setName("Kalyan");
        brand.setBrandCode("KLYN-01");
        brand.setActive(true);
        return brandRepository.save(brand);
    }

    private Product product(Brand brand, String name, String sku, String barcode) {
        Product product = new Product();
        product.setBrand(brand);
        product.setName(name);
        product.setSku(sku);
        product.setBarcode(barcode);
        product.setPurchasePrice(new BigDecimal("4000.00"));
        product.setSellingPrice(new BigDecimal("5000.00"));
        product.setStockQuantity(100);
        product.setActive(true);
        return product;
    }

    @Test
    void sku_shouldBeUnique() {
        Brand brand = saveBrand();
        productRepository.save(product(brand, "Gold Bangle", "GB-001", "890000001"));

        assertThatThrownBy(() -> productRepository.save(
                product(brand, "Gold Bangle 2", "GB-001", "890000002")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void search_shouldFindByNameIgnoringCase() {
        Brand brand = saveBrand();
        productRepository.save(product(brand, "Gold Bangle", "GB-001", "890000001"));
        productRepository.save(product(brand, "Silver Ring", "SR-002", "890000002"));

        Page<Product> result = productRepository.search(
                "bangle", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Gold Bangle");
    }

    @Test
    void pagination_shouldRespectPageSize() {
        Brand brand = saveBrand();
        for (int i = 1; i <= 5; i++) {
            productRepository.save(product(brand, "Item " + i, "SKU-" + i, "BAR-" + i));
        }

        Page<Product> page = productRepository.findByActiveTrue(
                PageRequest.of(0, 2, Sort.by("id").ascending()));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.isLast()).isFalse();
    }

    @Test
    void search_shouldNotReturnInactiveProducts() {
        Brand brand = saveBrand();
        Product inactive = product(brand, "Gold Bangle", "GB-001", "890000001");
        inactive.setActive(false);
        productRepository.save(inactive);

        Page<Product> result = productRepository.search(
                "bangle", PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }
}
