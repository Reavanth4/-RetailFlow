package com.retailflow.billingservice.billing.mapper;

import com.retailflow.billingservice.billing.dto.request.PaymentCreateRequest;
import com.retailflow.billingservice.billing.dto.response.PaymentResponse;
import com.retailflow.billingservice.billing.entity.Payment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    Payment toEntity(PaymentCreateRequest request);

    PaymentResponse toResponse(Payment payment);

    List<PaymentResponse> toResponseList(List<Payment> payments);
}
