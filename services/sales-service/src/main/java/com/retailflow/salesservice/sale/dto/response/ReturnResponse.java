package com.retailflow.salesservice.sale.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReturnResponse {

    private Long id;

    private Long saleId;

    private String returnNumber;

    private LocalDate returnDate;

    private String reason;

    private List<ReturnItemResponse> items;

    private LocalDateTime createdAt;
}
