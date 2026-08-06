package com.retailflow.purchaseservice.purchase.mapper;

import com.retailflow.purchaseservice.purchase.dto.request.SupplierReturnCreateRequest;
import com.retailflow.purchaseservice.purchase.dto.request.SupplierReturnItemRequest;
import com.retailflow.purchaseservice.purchase.dto.response.SupplierReturnItemResponse;
import com.retailflow.purchaseservice.purchase.dto.response.SupplierReturnResponse;
import com.retailflow.purchaseservice.purchase.entity.SupplierReturn;
import com.retailflow.purchaseservice.purchase.entity.SupplierReturnItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierReturnMapper {

    SupplierReturn toEntity(SupplierReturnCreateRequest request);

    @Mapping(target = "supplierReturn", ignore = true)
    SupplierReturnItem toItemEntity(SupplierReturnItemRequest request);

    SupplierReturnResponse toResponse(SupplierReturn supplierReturn);

    SupplierReturnItemResponse toItemResponse(SupplierReturnItem supplierReturnItem);

    List<SupplierReturnResponse> toResponseList(List<SupplierReturn> supplierReturns);
}
