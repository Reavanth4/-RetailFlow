package com.retailflow.supplierservice.supplier.service.imp;

import com.retailflow.supplierservice.common.exception.DuplicateResourceException;
import com.retailflow.supplierservice.common.exception.ResourceNotFoundException;
import com.retailflow.supplierservice.supplier.dto.request.SupplierCreateRequest;
import com.retailflow.supplierservice.supplier.dto.request.SupplierUpdateRequest;
import com.retailflow.supplierservice.supplier.dto.response.SupplierResponse;
import com.retailflow.supplierservice.supplier.entity.Supplier;
import com.retailflow.supplierservice.supplier.mapper.SupplierMapper;
import com.retailflow.supplierservice.supplier.repository.SupplierRepository;
import com.retailflow.supplierservice.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponse createSupplier(SupplierCreateRequest request) {
        if (supplierRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Supplier name already exists.");
        }

        Supplier supplier = supplierMapper.toEntity(request);
        supplier.setActive(true);

        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override
    public SupplierResponse updateSupplier(Long supplierId, SupplierUpdateRequest request) {
        Supplier supplier = findSupplier(supplierId);

        if (request.getName() != null
                && !request.getName().equals(supplier.getName())
                && supplierRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Supplier name already exists.");
        }

        supplierMapper.updateEntityFromDto(request, supplier);

        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override
    public SupplierResponse getSupplierById(Long supplierId) {
        return supplierMapper.toResponse(findSupplier(supplierId));
    }

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteSupplier(Long supplierId) {
        Supplier supplier = findSupplier(supplierId);
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    private Supplier findSupplier(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + supplierId));
    }
}
