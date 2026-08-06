package com.retailflow.warehouseservice.warehouse.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WarehouseResponse {

    private Long id;

    private String name;

    private String code;

    private String location;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
