package com.retailflow.productservice.product.service;

import com.retailflow.productservice.common.dto.PageResponse;
import com.retailflow.productservice.product.dto.request.ProductCreateRequest;
import com.retailflow.productservice.product.dto.request.ProductUpdateRequest;
import com.retailflow.productservice.product.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse updateProduct(Long productId,
                                  ProductUpdateRequest request);

    ProductResponse getProductById(Long productId);

    PageResponse<ProductResponse> getAllProducts(String search, Pageable pageable);

    void deleteProduct(Long productId);
}
