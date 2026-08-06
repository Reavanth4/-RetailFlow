package com.retailflow.customerservice.customer.controller;

import com.retailflow.customerservice.common.response.ApiResponse;
import com.retailflow.customerservice.customer.dto.request.CustomerCreateRequest;
import com.retailflow.customerservice.customer.dto.request.CustomerUpdateRequest;
import com.retailflow.customerservice.customer.dto.response.CustomerResponse;
import com.retailflow.customerservice.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerCreateRequest request) {

        return ApiResponse.success(
                "Customer created successfully",
                customerService.createCustomer(request)
        );
    }

    @GetMapping
    public ApiResponse<List<CustomerResponse>> getAllCustomers() {

        return ApiResponse.success(
                "Customers fetched successfully",
                customerService.getAllCustomers()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> getCustomerById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Customer fetched successfully",
                customerService.getCustomerById(id)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {

        return ApiResponse.success(
                "Customer updated successfully",
                customerService.updateCustomer(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCustomer(
            @PathVariable Long id) {

        customerService.deleteCustomer(id);

        return ApiResponse.success(
                "Customer deleted successfully",
                "Deleted Successfully"
        );
    }
}
