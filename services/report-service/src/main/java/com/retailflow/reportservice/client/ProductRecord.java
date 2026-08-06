package com.retailflow.reportservice.client;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductRecord(Long id,
                            String sku,
                            String barcode,
                            String name,
                            BigDecimal purchasePrice,
                            BigDecimal sellingPrice,
                            Boolean active) {
}
