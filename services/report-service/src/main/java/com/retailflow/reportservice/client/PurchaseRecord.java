package com.retailflow.reportservice.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PurchaseRecord(Long id,
                             String purchaseNumber,
                             Long supplierId,
                             Long warehouseId,
                             LocalDate purchaseDate,
                             BigDecimal subtotal,
                             BigDecimal tax,
                             BigDecimal discount,
                             BigDecimal total,
                             String status,
                             List<PurchaseItemRecord> items) {
}
