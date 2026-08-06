package com.retailflow.productservice.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailflow.productservice.product.dto.request.ProductCreateRequest;
import com.retailflow.productservice.product.dto.response.ProductResponse;
import com.retailflow.productservice.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductCreateRequest validRequest() {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Gold Bangle");
        request.setSku("GB-001");
        request.setBarcode("890000001");
        request.setPurchasePrice(new BigDecimal("4000.00"));
        request.setSellingPrice(new BigDecimal("5000.00"));
        request.setStockQuantity(100);
        request.setBrandId(1L);
        return request;
    }

    @Test
    void createProduct_shouldReturn201WithApiResponseStructure() throws Exception {
        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Gold Bangle");

        when(productService.createProduct(any(ProductCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Gold Bangle"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createProduct_shouldReturn400_whenValidationFails() throws Exception {
        ProductCreateRequest request = validRequest();
        request.setName("");

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllProducts_shouldReturn200WithPagingMetadata() throws Exception {
        com.retailflow.productservice.common.dto.PageResponse<ProductResponse> page =
                com.retailflow.productservice.common.dto.PageResponse.<ProductResponse>builder()
                        .content(java.util.List.of())
                        .page(0)
                        .size(5)
                        .totalElements(0)
                        .totalPages(0)
                        .last(true)
                        .build();

        when(productService.getAllProducts(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/products")
                        .param("search", "bangle")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.last").value(true));
    }
}
