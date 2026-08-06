package com.retailflow.productservice.product.service.imp;

import com.retailflow.productservice.brand.entity.Brand;
import com.retailflow.productservice.brand.repository.BrandRepository;
import com.retailflow.productservice.common.exception.DuplicateResourceException;
import com.retailflow.productservice.common.exception.ResourceNotFoundException;
import com.retailflow.productservice.product.dto.request.ProductCreateRequest;
import com.retailflow.productservice.product.entity.Product;
import com.retailflow.productservice.product.mapper.ProductMapper;
import com.retailflow.productservice.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductMapper productMapper;

    private ProductServiceImpl productService;

    private Brand brand;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, brandRepository, productMapper);

        brand = Brand.builder()
                .name("Kalyan")
                .brandCode("KLY")
                .active(true)
                .build();
        brand.setId(1L);
    }

    @Test
    void createProduct_shouldSaveProduct_whenSkuAndBarcodeAreUnique() {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Gold Bangle");
        request.setSku("GB-001");
        request.setBarcode("890000001");
        request.setPurchasePrice(new BigDecimal("4000.00"));
        request.setSellingPrice(new BigDecimal("5000.00"));
        request.setStockQuantity(100);
        request.setBrandId(1L);

        Product product = Product.builder()
                .name("Gold Bangle")
                .sku("GB-001")
                .barcode("890000001")
                .purchasePrice(new BigDecimal("4000.00"))
                .sellingPrice(new BigDecimal("5000.00"))
                .stockQuantity(100)
                .build();

        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productMapper.toResponse(any(Product.class)))
                .thenReturn(new com.retailflow.productservice.product.dto.response.ProductResponse());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        productService.createProduct(request);

        verify(productRepository).save(any(Product.class));
        assertThat(product.getBrand()).isEqualTo(brand);
        assertThat(product.getActive()).isTrue();
    }

    @Test
    void createProduct_shouldThrow_whenSkuAlreadyExists() {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setSku("GB-001");
        request.setBarcode("890000001");
        request.setName("Gold Bangle");
        request.setBrandId(1L);

        when(productRepository.existsBySku("GB-001")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("SKU");
    }

    @Test
    void createProduct_shouldThrow_whenBrandNotFound() {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setSku("GB-001");
        request.setBarcode("890000001");
        request.setName("Gold Bangle");
        request.setBrandId(999L);

        when(productRepository.existsBySku(any())).thenReturn(false);
        when(productRepository.existsByBarcode(any())).thenReturn(false);
        when(brandRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Brand not found");
    }

    @Test
    void getProductById_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void deleteProduct_shouldSoftDeleteProduct() {
        Product product = Product.builder().active(true).build();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        productService.deleteProduct(1L);

        assertThat(product.getActive()).isFalse();
        verify(productRepository).save(product);
    }
}
