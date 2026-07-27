package com.retailflow.productservice.brand.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandResponse {

    private Long id;

    private String name;

    private String brandCode;

    private String description;

    private Boolean active;

}