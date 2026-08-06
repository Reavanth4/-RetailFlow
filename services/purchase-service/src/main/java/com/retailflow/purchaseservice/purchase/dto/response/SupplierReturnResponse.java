package com.retailflow.purchaseservice.purchase.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class SupplierReturnResponse {

    private Long id;
    private String returnNumber;
    private Long purchaseId;
    private Long supplierId;
    private Long warehouseId;
    private LocalDate returnDate;
    private List<SupplierReturnItemResponse> items = new ArrayList<>();
}
