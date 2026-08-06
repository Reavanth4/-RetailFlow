package com.retailflow.inventoryservice.inventory.mapper;

import com.retailflow.inventoryservice.inventory.dto.response.InventoryResponse;
import com.retailflow.inventoryservice.inventory.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryResponse toResponse(Inventory inventory);
}
