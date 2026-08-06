package com.retailflow.billingservice.billing.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class BillCreateRequest {

    @NotNull(message = "Sale id is required")
    private Long saleId;

    @NotNull(message = "Customer id is required")
    private Long customerId;

    private LocalDate billDate;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Tax cannot be negative")
    private BigDecimal tax = BigDecimal.ZERO;

    @NotEmpty(message = "At least one item is required")
    private List<BillItemRequest> items = new ArrayList<>();
}
