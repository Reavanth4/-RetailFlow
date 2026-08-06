package com.retailflow.productservice.product.controller;

import com.retailflow.productservice.common.dto.PageResponse;
import com.retailflow.productservice.common.response.ApiResponse;
import com.retailflow.productservice.product.dto.request.ProductCreateRequest;
import com.retailflow.productservice.product.dto.request.ProductUpdateRequest;
import com.retailflow.productservice.product.dto.response.ProductResponse;
import com.retailflow.productservice.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Create Product
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        return ApiResponse.success(
                "Product created successfully",
                productService.createProduct(request)
        );
    }

    /**
     * Get All Products (search + pagination)
     */
    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        String[] sortParts = sort.split(",");
        String sortBy = sortParts[0];
        Sort.Direction sortDir = (sortParts.length > 1
                && sortParts[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));

        return ApiResponse.success(
                "Products fetched successfully",
                productService.getAllProducts(search, pageable)
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