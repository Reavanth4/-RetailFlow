package com.retailflow.warehouseservice.warehouse.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseUpdateRequest {

    @Size(max = 100, message = "Warehouse name cannot exceed 100 characters")
    private String name;

    @Size(max = 20, message = "Warehouse code cannot exceed 20 characters")
    private String code;

    @Size(max = 255, message = "Location cannot exceed 255 characters")
    private String location;
}
