package com.retailflow.reportservice.client;

import java.math.BigDecimal;

public record SaleItemRecord(Long id,
                             Long productId,
                             Integer quantity,
                             BigDecimal unitPrice,
                             BigDecimal discount,
                             BigDecimal tax,
                             BigDecimal total) {
}
