package com.retailflow.productservice.product.controller;

import com.retailflow.productservice.common.response.ApiResponse;
import com.retailflow.productservice.product.dto.request.ProductCreateRequest;
import com.retailflow.productservice.product.dto.response.ProductResponse;
import com.retailflow.productservice.product.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class    ProductController
{

    ProductService service;
    @PostMapping("/api/v1/products")
    public ApiResponse<ProductResponse> createResponse( @RequestBody ProductCreateRequest request)
    {
        ProductResponse response = service.createProduct(request);
        Logger log=LoggerFactory.getLogger(ProductController.class);
        log.info("This is Product Response");
        return ApiResponse.success("Product Created Successfully" , response);
    }

    @GetMapping
    public String display()
    {
        return "helle";
    }

    @GetMapping("/id" )
    public String display(int id)
    {
        return "hello 1";
    }
}
