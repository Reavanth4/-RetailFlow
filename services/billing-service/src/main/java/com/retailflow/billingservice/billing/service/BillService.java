package com.retailflow.billingservice.billing.service;

import com.retailflow.billingservice.billing.dto.request.BillCreateRequest;
import com.retailflow.billingservice.billing.dto.response.BillResponse;

import java.util.List;

public interface BillService {

    BillResponse createBill(BillCreateRequest request);

    BillResponse getBillById(Long id);

    List<BillResponse> getAllBills();

    List<BillResponse> getBillsBySale(Long saleId);

    String printBill(Long id);
}
