package com.retailflow.customerservice.customer.service;

import com.retailflow.customerservice.customer.dto.request.CustomerCreateRequest;
import com.retailflow.customerservice.customer.dto.request.CustomerUpdateRequest;
import com.retailflow.customerservice.customer.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerCreateRequest request);

    CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request);

    CustomerResponse getCustomerById(Long customerId);

    List<CustomerResponse> getAllCustomers();

    void deleteCustomer(Long customerId);
}
