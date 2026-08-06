package com.retailflow.purchaseservice.purchase.controller;

import com.retailflow.purchaseservice.common.response.ApiResponse;
import com.retailflow.purchaseservice.purchase.dto.request.PurchaseCreateRequest;
import com.retailflow.purchaseservice.purchase.dto.request.PurchaseUpdateRequest;
import com.retailflow.purchaseservice.purchase.dto.response.PurchaseResponse;
import com.retailflow.purchaseservice.purchase.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PurchaseResponse> createPurchase(
            @Valid @RequestBody PurchaseCreateRequest request) {

        return ApiResponse.success(
                "Purchase created successfully",
                purchaseService.createPurchase(request)
        );
    }

    @GetMapping
    public ApiResponse<List<PurchaseResponse>> getAllPurchases() {

        return ApiResponse.success(
                "Purchases fetched successfully",
                purchaseService.getAllPurchases()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<PurchaseResponse> getPurchaseById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Purchase fetched successfully",
                purchaseService.getPurchaseById(id)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<PurchaseResponse> updatePurchase(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseUpdateRequest request) {

        return ApiResponse.success(
                "Purchase updated successfully",
                purchaseService.updatePurchase(id, request)
        );
    }

    @PostMapping("/{id}/order")
    public ApiResponse<PurchaseResponse> orderPurchase(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Purchase ordered successfully",
                purchaseService.orderPurchase(id)
        );
    }

    @PostMapping("/{id}/receive")
    public ApiResponse<PurchaseResponse> receivePurchase(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Goods received, inventory updated",
                purchaseService.receivePurchase(id)
        );
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<PurchaseResponse> cancelPurchase(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Purchase cancelled successfully",
                purchaseService.cancelPurchase(id)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deletePurchase(
            @PathVariable Long id) {

        purchaseService.deletePurchase(id);

        return ApiResponse.success(
                "Purchase deleted successfully",
                "Deleted Successfully"
        );
    }
}
