package com.retailflow.purchaseservice.purchase.service;

import com.retailflow.purchaseservice.purchase.dto.request.SupplierReturnCreateRequest;
import com.retailflow.purchaseservice.purchase.dto.response.SupplierReturnResponse;

import java.util.List;

public interface SupplierReturnService {

    SupplierReturnResponse createSupplierReturn(Long purchaseId, SupplierReturnCreateRequest request);

    SupplierReturnResponse getSupplierReturnById(Long returnId);

    List<SupplierReturnResponse> getAllSupplierReturns();

    List<SupplierReturnResponse> getReturnsByPurchase(Long purchaseId);
}
