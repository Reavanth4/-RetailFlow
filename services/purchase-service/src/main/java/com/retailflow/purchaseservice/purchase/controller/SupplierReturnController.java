package com.retailflow.purchaseservice.purchase.controller;

import com.retailflow.purchaseservice.common.response.ApiResponse;
import com.retailflow.purchaseservice.purchase.dto.request.SupplierReturnCreateRequest;
import com.retailflow.purchaseservice.purchase.dto.response.SupplierReturnResponse;
import com.retailflow.purchaseservice.purchase.service.SupplierReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SupplierReturnController {

    private final SupplierReturnService supplierReturnService;

    @PostMapping("/purchases/{purchaseId}/returns")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SupplierReturnResponse> createSupplierReturn(
            @PathVariable Long purchaseId,
            @Valid @RequestBody SupplierReturnCreateRequest request) {

        return ApiResponse.success(
                "Supplier return created, stock deducted",
                supplierReturnService.createSupplierReturn(purchaseId, request)
        );
    }

    @GetMapping("/purchases/{purchaseId}/returns")
    public ApiResponse<List<SupplierReturnResponse>> getReturnsByPurchase(
            @PathVariable Long purchaseId) {

        return ApiResponse.success(
                "Returns fetched successfully",
                supplierReturnService.getReturnsByPurchase(purchaseId)
        );
    }

    @GetMapping("/purchase-returns")
    public ApiResponse<List<SupplierReturnResponse>> getAllSupplierReturns() {

        return ApiResponse.success(
                "Returns fetched successfully",
                supplierReturnService.getAllSupplierReturns()
        );
    }

    @GetMapping("/purchase-returns/{id}")
    public ApiResponse<SupplierReturnResponse> getSupplierReturnById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Return fetched successfully",
                supplierReturnService.getSupplierReturnById(id)
        );
    }
}
