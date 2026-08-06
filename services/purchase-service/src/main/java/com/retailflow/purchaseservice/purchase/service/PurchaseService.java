package com.retailflow.purchaseservice.purchase.service;

import com.retailflow.purchaseservice.purchase.dto.request.PurchaseCreateRequest;
import com.retailflow.purchaseservice.purchase.dto.request.PurchaseUpdateRequest;
import com.retailflow.purchaseservice.purchase.dto.response.PurchaseResponse;

import java.util.List;

public interface PurchaseService {

    PurchaseResponse createPurchase(PurchaseCreateRequest request);

    PurchaseResponse updatePurchase(Long purchaseId, PurchaseUpdateRequest request);

    PurchaseResponse getPurchaseById(Long purchaseId);

    List<PurchaseResponse> getAllPurchases();

    PurchaseResponse orderPurchase(Long purchaseId);

    PurchaseResponse receivePurchase(Long purchaseId);

    PurchaseResponse cancelPurchase(Long purchaseId);

    void deletePurchase(Long purchaseId);
}
