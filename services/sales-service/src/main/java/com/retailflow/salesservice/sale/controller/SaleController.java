package com.retailflow.salesservice.sale.controller;

import com.retailflow.salesservice.common.response.ApiResponse;
import com.retailflow.salesservice.sale.dto.request.ReturnCreateRequest;
import com.retailflow.salesservice.sale.dto.request.SaleCreateRequest;
import com.retailflow.salesservice.sale.dto.request.SaleUpdateRequest;
import com.retailflow.salesservice.sale.dto.response.ReturnResponse;
import com.retailflow.salesservice.sale.dto.response.SaleResponse;
import com.retailflow.salesservice.sale.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SaleResponse> createSale(
            @Valid @RequestBody SaleCreateRequest request) {

        return ApiResponse.success(
                "Sale created successfully",
                saleService.createSale(request)
        );
    }

    @GetMapping
    public ApiResponse<List<SaleResponse>> getAllSales() {

        return ApiResponse.success(
                "Sales fetched successfully",
                saleService.getAllSales()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<SaleResponse> getSaleById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Sale fetched successfully",
                saleService.getSaleById(id)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<SaleResponse> updateSale(
            @PathVariable Long id,
            @Valid @RequestBody SaleUpdateRequest request) {

        return ApiResponse.success(
                "Sale updated successfully",
                saleService.updateSale(id, request)
        );
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<SaleResponse> completeSale(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Sale completed, stock deducted",
                saleService.completeSale(id)
        );
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<SaleResponse> cancelSale(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Sale cancelled successfully",
                saleService.cancelSale(id)
        );
    }

    @PostMapping("/{id}/returns")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReturnResponse> createReturn(
            @PathVariable Long id,
            @Valid @RequestBody ReturnCreateRequest request) {

        return ApiResponse.success(
                "Return created, stock restored",
                saleService.createReturn(id, request)
        );
    }

    @GetMapping("/{id}/returns")
    public ApiResponse<List<ReturnResponse>> getReturnsBySale(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Returns fetched successfully",
                saleService.getReturnsBySale(id)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteSale(
            @PathVariable Long id) {

        saleService.deleteSale(id);

        return ApiResponse.success(
                "Sale deleted successfully",
                "Deleted Successfully"
        );
    }
}
