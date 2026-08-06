package com.retailflow.purchaseservice.purchase.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class SupplierReturnCreateRequest {

    private LocalDate returnDate;

    @NotEmpty(message = "At least one return item is required")
    private List<SupplierReturnItemRequest> items = new ArrayList<>();
}
