package com.retailflow.warehouseservice.warehouse.service.imp;

import com.retailflow.warehouseservice.common.exception.DuplicateResourceException;
import com.retailflow.warehouseservice.common.exception.ResourceNotFoundException;
import com.retailflow.warehouseservice.warehouse.dto.request.WarehouseCreateRequest;
import com.retailflow.warehouseservice.warehouse.dto.request.WarehouseUpdateRequest;
import com.retailflow.warehouseservice.warehouse.dto.response.WarehouseResponse;
import com.retailflow.warehouseservice.warehouse.entity.Warehouse;
import com.retailflow.warehouseservice.warehouse.mapper.WarehouseMapper;
import com.retailflow.warehouseservice.warehouse.repository.WarehouseRepository;
import com.retailflow.warehouseservice.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    private final WarehouseMapper warehouseMapper;

    @Override
    public WarehouseResponse createWarehouse(WarehouseCreateRequest request) {
        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Warehouse code already exists.");
        }
        if (warehouseRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Warehouse name already exists.");
        }

        Warehouse warehouse = warehouseMapper.toEntity(request);
        warehouse.setActive(true);

        return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    @Override
    public WarehouseResponse updateWarehouse(Long warehouseId, WarehouseUpdateRequest request) {
        Warehouse warehouse = findWarehouse(warehouseId);

        if (request.getCode() != null
                && !request.getCode().equals(warehouse.getCode())
                && warehouseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Warehouse code already exists.");
        }
        if (request.getName() != null
                && !request.getName().equals(warehouse.getName())
                && warehouseRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Warehouse name already exists.");
        }

        warehouseMapper.updateEntityFromDto(request, warehouse);

        return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    @Override
    public WarehouseResponse getWarehouseById(Long warehouseId) {
        return warehouseMapper.toResponse(findWarehouse(warehouseId));
    }

    @Override
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteWarehouse(Long warehouseId) {
        Warehouse warehouse = findWarehouse(warehouseId);
        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
    }

    private Warehouse findWarehouse(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found with id: " + warehouseId));
    }
}
