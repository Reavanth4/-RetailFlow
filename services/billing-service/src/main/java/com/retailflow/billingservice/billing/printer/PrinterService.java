package com.retailflow.billingservice.billing.printer;

import com.retailflow.billingservice.billing.dto.response.BillResponse;

public interface PrinterService {

    String print(BillResponse bill);
}
