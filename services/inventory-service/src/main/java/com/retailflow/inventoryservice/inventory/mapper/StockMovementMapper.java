package com.retailflow.inventoryservice.inventory.mapper;

import com.retailflow.inventoryservice.inventory.dto.response.StockMovementResponse;
import com.retailflow.inventoryservice.inventory.entity.StockMovement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {

    StockMovementResponse toResponse(StockMovement stockMovement);
}
