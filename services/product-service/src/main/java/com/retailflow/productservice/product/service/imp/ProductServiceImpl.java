package com.retailflow.productservice.product.service.imp;

import com.retailflow.productservice.brand.entity.Brand;
import com.retailflow.productservice.brand.repository.BrandRepository;
import com.retailflow.productservice.common.dto.PageResponse;
import com.retailflow.productservice.common.exception.DuplicateResourceException;
import com.retailflow.productservice.common.exception.ResourceNotFoundException;
import com.retailflow.productservice.product.dto.request.ProductCreateRequest;
import com.retailflow.productservice.product.dto.request.ProductUpdateRequest;
import com.retailflow.productservice.product.dto.response.ProductResponse;
import com.retailflow.productservice.product.entity.Product;
import com.retailflow.productservice.product.mapper.ProductMapper;
import com.retailflow.productservice.product.repository.ProductRepository;
import com.retailflow.productservice.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ProductResponse createProduct(ProductCreateRequest request) {
        validateUniqueSkuAndBarcode(request.getSku(), request.getBarcode());

        Brand brand = findBrand(request.getBrandId());

        Product product = productMapper.toEntity(request);
        product.setBrand(brand);
        product.setActive(true);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = findProduct(productId);

        if (request.getSku() != null
                && !request.getSku().equals(product.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product SKU already exists.");
        }
        if (request.getBarcode() != null
                && !request.getBarcode().equals(product.getBarcode())
                && productRepository.existsByBarcode(request.getBarcode())) {
            throw new DuplicateResourceException("Product barcode already exists.");
        }

        productMapper.updateEntityFromDto(request, product);

        if (request.getBrandId() != null) {
            product.setBrand(findBrand(request.getBrandId()));
        }

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return productMapper.toResponse(findProduct(productId));
    }

    @Override
    public PageResponse<ProductResponse> getAllProducts(String search, Pageable pageable) {
        Page<Product> page = (search == null || search.isBlank())
                ? productRepository.findByActiveTrue(pageable)
                : productRepository.search(search.toLowerCase(), pageable);

        List<ProductResponse> content = page.getContent().stream()
                .map(productMapper::toResponse)
                .toList();

        return PageResponse.from(page, content);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);
        product.setActive(false);
        productRepository.save(product);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));
    }

    private Brand findBrand(Long brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Brand not found with id: " + brandId));
    }

    private void validateUniqueSkuAndBarcode(String sku, String barcode) {
        if (productRepository.existsBySku(sku)) {
            throw new DuplicateResourceException("Product SKU already exists.");
        }
        if (productRepository.existsByBarcode(barcode)) {
            throw new DuplicateResourceException("Product barcode already exists.");
        }
    }
}
