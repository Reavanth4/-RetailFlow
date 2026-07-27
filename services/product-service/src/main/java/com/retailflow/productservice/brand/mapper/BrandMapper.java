package com.retailflow.productservice.brand.mapper;

import com.retailflow.productservice.brand.dto.request.BrandCreateRequest;
import com.retailflow.productservice.brand.dto.request.BrandUpdateRequest;
import com.retailflow.productservice.brand.dto.response.BrandResponse;
import com.retailflow.productservice.brand.entity.Brand;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    /**
     * Convert Create Request DTO -> Entity
     */
    Brand toEntity(BrandCreateRequest request);

    /**
     * Convert Entity -> Response DTO
     */
    BrandResponse toResponse(Brand brand);

    /**
     * Update existing Brand Entity
     */
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntityFromDto(
            BrandUpdateRequest request,
            @MappingTarget Brand brand
    );

}