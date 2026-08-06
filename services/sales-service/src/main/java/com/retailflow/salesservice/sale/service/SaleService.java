package com.retailflow.salesservice.sale.service;

import com.retailflow.salesservice.sale.dto.request.ReturnCreateRequest;
import com.retailflow.salesservice.sale.dto.request.SaleCreateRequest;
import com.retailflow.salesservice.sale.dto.request.SaleUpdateRequest;
import com.retailflow.salesservice.sale.dto.response.ReturnResponse;
import com.retailflow.salesservice.sale.dto.response.SaleResponse;

import java.util.List;

public interface SaleService {

    SaleResponse createSale(SaleCreateRequest request);

    SaleResponse updateSale(Long saleId, SaleUpdateRequest request);

    SaleResponse getSaleById(Long saleId);

    List<SaleResponse> getAllSales();

    SaleResponse completeSale(Long saleId);

    SaleResponse cancelSale(Long saleId);

    void deleteSale(Long saleId);

    ReturnResponse createReturn(Long saleId, ReturnCreateRequest request);

    List<ReturnResponse> getReturnsBySale(Long saleId);
}
