package com.retailflow.customerservice.customer.service.imp;

import com.retailflow.customerservice.common.exception.DuplicateResourceException;
import com.retailflow.customerservice.common.exception.ResourceNotFoundException;
import com.retailflow.customerservice.customer.dto.request.CustomerCreateRequest;
import com.retailflow.customerservice.customer.dto.request.CustomerUpdateRequest;
import com.retailflow.customerservice.customer.dto.response.CustomerResponse;
import com.retailflow.customerservice.customer.entity.Customer;
import com.retailflow.customerservice.customer.mapper.CustomerMapper;
import com.retailflow.customerservice.customer.repository.CustomerRepository;
import com.retailflow.customerservice.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        if (customerRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Customer name already exists.");
        }

        Customer customer = customerMapper.toEntity(request);
        customer.setActive(true);

        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request) {
        Customer customer = findCustomer(customerId);

        if (request.getName() != null
                && !request.getName().equals(customer.getName())
                && customerRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Customer name already exists.");
        }

        customerMapper.updateEntityFromDto(request, customer);

        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        return customerMapper.toResponse(findCustomer(customerId));
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteCustomer(Long customerId) {
        Customer customer = findCustomer(customerId);
        customer.setActive(false);
        customerRepository.save(customer);
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + customerId));
    }
}
