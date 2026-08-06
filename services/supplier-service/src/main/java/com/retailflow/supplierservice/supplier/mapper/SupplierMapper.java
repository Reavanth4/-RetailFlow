package com.retailflow.supplierservice.supplier.mapper;

import com.retailflow.supplierservice.supplier.dto.request.SupplierCreateRequest;
import com.retailflow.supplierservice.supplier.dto.request.SupplierUpdateRequest;
import com.retailflow.supplierservice.supplier.dto.response.SupplierResponse;
import com.retailflow.supplierservice.supplier.entity.Supplier;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toEntity(SupplierCreateRequest request);

    SupplierResponse toResponse(Supplier supplier);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(SupplierUpdateRequest request,
                             @MappingTarget Supplier supplier);
}
