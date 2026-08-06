package com.retailflow.productservice.brand.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandUpdateRequest {

    @Size(max = 100, message = "Brand name cannot exceed 100 characters")
    private String name;

    @Size(max = 20, message = "Brand code cannot exceed 20 characters")
    private String brandCode;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
