package com.retailflow.productservice.product.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductResponse {

    private Long id;

    private String sku;

    private String barcode;

    private String name;

    private String description;

    private BigDecimal purchasePrice;

    private BigDecimal sellingPrice;

    private Integer stockQuantity;

    private Long brandId;

    private String brandName;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}