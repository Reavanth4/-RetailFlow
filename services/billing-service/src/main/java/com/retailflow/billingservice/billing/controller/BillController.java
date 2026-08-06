package com.retailflow.billingservice.billing.controller;

import com.retailflow.billingservice.billing.dto.request.BillCreateRequest;
import com.retailflow.billingservice.billing.dto.response.BillResponse;
import com.retailflow.billingservice.billing.service.BillService;
import com.retailflow.billingservice.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BillResponse> createBill(
            @Valid @RequestBody BillCreateRequest request) {

        return ApiResponse.success(
                "Bill created successfully",
                billService.createBill(request)
        );
    }

    @GetMapping
    public ApiResponse<List<BillResponse>> getAllBills() {

        return ApiResponse.success(
                "Bills fetched successfully",
                billService.getAllBills()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<BillResponse> getBillById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Bill fetched successfully",
                billService.getBillById(id)
        );
    }

    @GetMapping("/sale/{saleId}")
    public ApiResponse<List<BillResponse>> getBillsBySale(
            @PathVariable Long saleId) {

        return ApiResponse.success(
                "Bills fetched successfully",
                billService.getBillsBySale(saleId)
        );
    }

    @PostMapping("/{id}/print")
    public ApiResponse<String> printBill(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Bill printed successfully",
                billService.printBill(id)
        );
    }
}
