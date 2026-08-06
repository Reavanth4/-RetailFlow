package com.retailflow.salesservice.sale.mapper;

import com.retailflow.salesservice.sale.dto.response.SaleItemResponse;
import com.retailflow.salesservice.sale.dto.response.SaleResponse;
import com.retailflow.salesservice.sale.entity.Sale;
import com.retailflow.salesservice.sale.entity.SaleItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    SaleResponse toResponse(Sale sale);

    SaleItemResponse toItemResponse(SaleItem saleItem);
}
