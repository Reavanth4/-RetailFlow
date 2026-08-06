package com.retailflow.supplierservice.supplier.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SupplierResponse {

    private Long id;

    private String name;

    private String phone;

    private String email;

    private String address;

    private String taxNumber;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
