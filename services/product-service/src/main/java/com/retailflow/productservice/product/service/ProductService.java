package com.retailflow.productservice.product.service;

import com.retailflow.productservice.product.dto.request.ProductCreateRequest;
import com.retailflow.productservice.product.dto.request.ProductUpdateRequest;
import com.retailflow.productservice.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse updateProduct(Long productId,
                                  ProductUpdateRequest request);

    ProductResponse getProductById(Long productId);

    List<ProductResponse> getAllProducts();

    void deleteProduct(Long productId);

}