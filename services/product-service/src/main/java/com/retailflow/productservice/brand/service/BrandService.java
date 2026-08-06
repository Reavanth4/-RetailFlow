package com.retailflow.productservice.brand.service;

import com.retailflow.productservice.brand.dto.request.BrandCreateRequest;
import com.retailflow.productservice.brand.dto.request.BrandUpdateRequest;
import com.retailflow.productservice.brand.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {

    BrandResponse createBrand(BrandCreateRequest request);

    BrandResponse updateBrand(Long brandId, BrandUpdateRequest request);

    BrandResponse getBrandById(Long brandId);

    List<BrandResponse> getAllBrands();

    void deleteBrand(Long brandId);
}
