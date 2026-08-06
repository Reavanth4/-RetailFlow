package com.retailflow.customerservice.customer.mapper;

import com.retailflow.customerservice.customer.dto.request.CustomerCreateRequest;
import com.retailflow.customerservice.customer.dto.request.CustomerUpdateRequest;
import com.retailflow.customerservice.customer.dto.response.CustomerResponse;
import com.retailflow.customerservice.customer.entity.Customer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerCreateRequest request);

    CustomerResponse toResponse(Customer customer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CustomerUpdateRequest request,
                             @MappingTarget Customer customer);
}
