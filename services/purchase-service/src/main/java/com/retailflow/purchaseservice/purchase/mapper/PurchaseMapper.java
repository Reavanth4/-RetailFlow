package com.retailflow.purchaseservice.purchase.mapper;

import com.retailflow.purchaseservice.purchase.dto.response.PurchaseItemResponse;
import com.retailflow.purchaseservice.purchase.dto.response.PurchaseResponse;
import com.retailflow.purchaseservice.purchase.entity.Purchase;
import com.retailflow.purchaseservice.purchase.entity.PurchaseItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    PurchaseResponse toResponse(Purchase purchase);

    PurchaseItemResponse toItemResponse(PurchaseItem purchaseItem);
}
