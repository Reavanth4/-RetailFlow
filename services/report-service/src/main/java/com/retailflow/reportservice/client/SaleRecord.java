package com.retailflow.reportservice.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SaleRecord(Long id,
                         String saleNumber,
                         Long customerId,
                         Long warehouseId,
                         LocalDate saleDate,
                         BigDecimal subtotal,
                         BigDecimal discount,
                         BigDecimal tax,
                         BigDecimal total,
                         String status,
                         List<SaleItemRecord> items) {
}
