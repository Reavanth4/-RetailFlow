package com.retailflow.salesservice.sale.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReturnCreateRequest {

    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;

    @Valid
    @NotEmpty(message = "Return must contain at least one item")
    private List<ReturnItemRequest> items;
}
