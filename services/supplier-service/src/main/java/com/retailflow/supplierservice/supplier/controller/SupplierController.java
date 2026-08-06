package com.retailflow.supplierservice.supplier.controller;

import com.retailflow.supplierservice.common.response.ApiResponse;
import com.retailflow.supplierservice.supplier.dto.request.SupplierCreateRequest;
import com.retailflow.supplierservice.supplier.dto.request.SupplierUpdateRequest;
import com.retailflow.supplierservice.supplier.dto.response.SupplierResponse;
import com.retailflow.supplierservice.supplier.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SupplierResponse> createSupplier(
            @Valid @RequestBody SupplierCreateRequest request) {

        return ApiResponse.success(
                "Supplier created successfully",
                supplierService.createSupplier(request)
        );
    }

    @GetMapping
    public ApiResponse<List<SupplierResponse>> getAllSuppliers() {

        return ApiResponse.success(
                "Suppliers fetched successfully",
                supplierService.getAllSuppliers()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<SupplierResponse> getSupplierById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Supplier fetched successfully",
                supplierService.getSupplierById(id)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<SupplierResponse> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierUpdateRequest request) {

        return ApiResponse.success(
                "Supplier updated successfully",
                supplierService.updateSupplier(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteSupplier(
            @PathVariable Long id) {

        supplierService.deleteSupplier(id);

        return ApiResponse.success(
                "Supplier deleted successfully",
                "Deleted Successfully"
        );
    }
}
