package com.retailflow.reportservice.client;

import java.math.BigDecimal;

public record PurchaseItemRecord(Long id,
                                 Long productId,
                                 Integer quantity,
                                 BigDecimal unitPrice,
                                 BigDecimal tax,
                                 BigDecimal discount,
                                 BigDecimal total) {
}
