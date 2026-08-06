package com.retailflow.productservice.product.mapper;

import com.retailflow.productservice.product.dto.request.ProductCreateRequest;
import com.retailflow.productservice.product.dto.request.ProductUpdateRequest;
import com.retailflow.productservice.product.dto.response.ProductResponse;
import com.retailflow.productservice.product.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Convert ProductCreateRequest -> Product Entity
     */
    @Mapping(target = "brand", ignore = true)
    Product toEntity(ProductCreateRequest request);

    /**
     * Convert Product Entity -> ProductResponse
     */
    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandName", source = "brand.name")
    ProductResponse toResponse(Product product);

    /**
     * Update existing Product Entity using ProductUpdateRequest.
     * Null values are ignored so partial updates are supported.
     */
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "brand", ignore = true)
    void updateEntityFromDto(ProductUpdateRequest request,
                             @MappingTarget Product product);

}
