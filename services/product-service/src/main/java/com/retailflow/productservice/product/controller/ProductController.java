package com.retailflow.productservice.product.controller;

import com.retailflow.productservice.common.response.ApiResponse;
import com.retailflow.productservice.product.dto.request.ProductCreateRequest;
import com.retailflow.productservice.product.dto.request.ProductUpdateRequest;
import com.retailflow.productservice.product.dto.response.ProductResponse;
import com.retailflow.productservice.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Create Product
     */
    @PostMapping
    public ProductResponse createFancyProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        ProductResponse response = productService.createProduct(request);

        return response;
    }

    /**
     * Get All Products
     */
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {

        return ApiResponse.success(
                "Products fetched successfully",
                productService.getAllProducts()
        );
    }

    /**
     * Get Product By Id
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Product fetched successfully",
                productService.getProductById(id)
        );
    }

    /**
     * Update Product
     */
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {

        ProductResponse response =
                productService.updateProduct(id, request);

        return ApiResponse.success(
                "Product updated successfully",
                response
        );
    }

    /**
     * Delete Product
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ApiResponse.success(
                "Product deleted successfully",
                "Deleted Successfully"
        );
    }
}