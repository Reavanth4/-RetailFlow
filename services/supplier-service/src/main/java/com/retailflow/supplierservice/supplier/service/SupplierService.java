package com.retailflow.supplierservice.supplier.service;

import com.retailflow.supplierservice.supplier.dto.request.SupplierCreateRequest;
import com.retailflow.supplierservice.supplier.dto.request.SupplierUpdateRequest;
import com.retailflow.supplierservice.supplier.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {

    SupplierResponse createSupplier(SupplierCreateRequest request);

    SupplierResponse updateSupplier(Long supplierId, SupplierUpdateRequest request);

    SupplierResponse getSupplierById(Long supplierId);

    List<SupplierResponse> getAllSuppliers();

    void deleteSupplier(Long supplierId);
}
