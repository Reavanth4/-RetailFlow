package com.retailflow.productservice.brand.controller;

import com.retailflow.productservice.brand.dto.request.BrandCreateRequest;
import com.retailflow.productservice.brand.dto.request.BrandUpdateRequest;
import com.retailflow.productservice.brand.dto.response.BrandResponse;
import com.retailflow.productservice.brand.service.BrandService;
import com.retailflow.productservice.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ApiResponse<BrandResponse> createBrand(
            @Valid @RequestBody BrandCreateRequest request) {

        return ApiResponse.success(
                "Brand created successfully",
                brandService.createBrand(request)
        );
    }

    @GetMapping
    public ApiResponse<List<BrandResponse>> getAllBrandsAPI() {

        return ApiResponse.success(
                "Brands fetched successfully",
                brandService.getAllBrands()
        );
    }



    @PutMapping("/{id}")
    public ApiResponse<BrandResponse> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandUpdateRequest request) {

        return ApiResponse.success(
                "Brand updated successfully",
                brandService.updateBrand(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBrand(
            @PathVariable Long id) {

        brandService.deleteBrand(id);

        return ApiResponse.success(
                "Brand deleted successfully",
                "Deleted Successfully"
        );
    }
}
