package com.retailflow.warehouseservice.warehouse.mapper;

import com.retailflow.warehouseservice.warehouse.dto.request.WarehouseCreateRequest;
import com.retailflow.warehouseservice.warehouse.dto.request.WarehouseUpdateRequest;
import com.retailflow.warehouseservice.warehouse.dto.response.WarehouseResponse;
import com.retailflow.warehouseservice.warehouse.entity.Warehouse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    Warehouse toEntity(WarehouseCreateRequest request);

    WarehouseResponse toResponse(Warehouse warehouse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(WarehouseUpdateRequest request,
                             @MappingTarget Warehouse warehouse);
}
