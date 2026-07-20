package com.retailflow.productservice.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreateRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "SKU is required")
    @Size(max = 30)
    private String sku;

    @NotBlank(message = "Barcode is required")
    @Size(max = 50)
    private String barcode;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal purchasePrice;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal sellingPrice;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0)
    private Integer stockQuantity;

    @NotNull(message = "Brand is required")
    private Long brandId;

}