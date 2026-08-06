package com.retailflow.customerservice.customer.service.imp;

import com.retailflow.customerservice.common.exception.DuplicateResourceException;
import com.retailflow.customerservice.common.exception.ResourceNotFoundException;
import com.retailflow.customerservice.customer.dto.request.CustomerCreateRequest;
import com.retailflow.customerservice.customer.entity.Customer;
import com.retailflow.customerservice.customer.mapper.CustomerMapper;
import com.retailflow.customerservice.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, customerMapper);
    }

    @Test
    void createCustomer_shouldSaveActiveCustomer() {
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setName("Ramesh Kumar");
        request.setPhone("9876543210");

        Customer customer = Customer.builder().name("Ramesh Kumar").build();

        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        customerService.createCustomer(request);

        assertThat(customer.getActive()).isTrue();
        verify(customerRepository).save(customer);
    }

    @Test
    void createCustomer_shouldThrow_whenNameExists() {
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setName("Ramesh Kumar");

        when(customerRepository.existsByName("Ramesh Kumar")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getCustomerById_shouldThrow_whenNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }
}
