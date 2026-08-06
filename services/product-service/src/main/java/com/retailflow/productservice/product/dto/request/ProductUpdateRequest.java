package com.retailflow.productservice.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {

    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    private String name;

    @Size(max = 30, message = "SKU cannot exceed 30 characters")
    private String sku;

    @Size(max = 50, message = "Barcode cannot exceed 50 characters")
    private String barcode;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.01", message = "Purchase price must be greater than zero")
    private BigDecimal purchasePrice;

    @DecimalMin(value = "0.01", message = "Selling price must be greater than zero")
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0", message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private Long brandId;
}
