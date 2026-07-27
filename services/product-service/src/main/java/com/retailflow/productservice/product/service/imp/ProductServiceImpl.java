package com.retailflow.productservice.product.service.imp;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.retailflow.productservice.brand.entity.Brand;
import com.retailflow.productservice.brand.repository.BrandRepository;
import com.retailflow.productservice.common.exception.ResourceNotFoundException;
import com.retailflow.productservice.product.dto.request.ProductCreateRequest;
import com.retailflow.productservice.product.dto.request.ProductUpdateRequest;
import com.retailflow.productservice.product.dto.response.ProductResponse;
import com.retailflow.productservice.product.entity.Product;
import com.retailflow.productservice.product.mapper.ProductMapper;
import com.retailflow.productservice.product.repository.ProductRepository;
import com.retailflow.productservice.product.service.ProductService;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
    public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final BrandRepository brandRepository;

    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
       if(productRepository.existsBySku(request.getSku()))
       {
           throw new DuplicateRequestException("Product sku already exists.");
       }
       if(productRepository.existsByBarcode(request.getBarcode()))
       {
           throw new DuplicateRequestException("Product sku already exists.");
       }
        Brand brand = brandRepository.findById(request.getBrandId()).orElseThrow(()->
                new ResourceNotFoundException("Brand Name not exists"));
        Product product = productMapper.toEntity(request);
        product.setBrand(brand);
        return productMapper.toResponse(productRepository.save(product));

    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        return null;
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return null;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return List.of();
    }

    @Override
    public void deleteProduct(Long productId) {

    }
    @Override
    public List<Product>getProducts(){
        return productRepository.findAll();
    }
}