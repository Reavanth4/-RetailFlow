package com.retailflow.billingservice.billing.mapper;

import com.retailflow.billingservice.billing.dto.request.BillCreateRequest;
import com.retailflow.billingservice.billing.dto.request.BillItemRequest;
import com.retailflow.billingservice.billing.dto.response.BillItemResponse;
import com.retailflow.billingservice.billing.dto.response.BillResponse;
import com.retailflow.billingservice.billing.entity.Bill;
import com.retailflow.billingservice.billing.entity.BillItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BillMapper {

    Bill toEntity(BillCreateRequest request);

    @Mapping(target = "bill", ignore = true)
    BillItem toItemEntity(BillItemRequest request);

    BillResponse toResponse(Bill bill);

    BillItemResponse toItemResponse(BillItem item);

    List<BillResponse> toResponseList(List<Bill> bills);
}
