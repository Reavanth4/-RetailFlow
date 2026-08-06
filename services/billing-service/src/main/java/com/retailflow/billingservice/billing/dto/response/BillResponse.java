package com.retailflow.billingservice.billing.dto.response;

import com.retailflow.billingservice.billing.entity.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class BillResponse {

    private Long id;
    private String invoiceNumber;
    private Long saleId;
    private Long customerId;
    private LocalDate billDate;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private PaymentStatus paymentStatus;
    private List<BillItemResponse> items = new ArrayList<>();
}
