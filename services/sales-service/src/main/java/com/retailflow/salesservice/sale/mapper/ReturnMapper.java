package com.retailflow.salesservice.sale.mapper;

import com.retailflow.salesservice.sale.dto.response.ReturnItemResponse;
import com.retailflow.salesservice.sale.dto.response.ReturnResponse;
import com.retailflow.salesservice.sale.entity.ReturnItem;
import com.retailflow.salesservice.sale.entity.SaleReturn;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReturnMapper {

    ReturnResponse toResponse(SaleReturn saleReturn);

    ReturnItemResponse toItemResponse(ReturnItem returnItem);
}
