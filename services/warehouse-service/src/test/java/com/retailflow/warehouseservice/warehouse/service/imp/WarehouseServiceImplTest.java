package com.retailflow.warehouseservice.warehouse.service.imp;

import com.retailflow.warehouseservice.common.exception.DuplicateResourceException;
import com.retailflow.warehouseservice.common.exception.ResourceNotFoundException;
import com.retailflow.warehouseservice.warehouse.dto.request.WarehouseCreateRequest;
import com.retailflow.warehouseservice.warehouse.entity.Warehouse;
import com.retailflow.warehouseservice.warehouse.mapper.WarehouseMapper;
import com.retailflow.warehouseservice.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    private WarehouseServiceImpl warehouseService;

    @BeforeEach
    void setUp() {
        warehouseService = new WarehouseServiceImpl(warehouseRepository, warehouseMapper);
    }

    @Test
    void createWarehouse_shouldSaveActiveWarehouse() {
        WarehouseCreateRequest request = new WarehouseCreateRequest();
        request.setName("Chennai Warehouse");
        request.setCode("CHN-01");
        request.setLocation("Chennai");

        Warehouse warehouse = Warehouse.builder()
                .name("Chennai Warehouse")
                .code("CHN-01")
                .location("Chennai")
                .build();

        when(warehouseMapper.toEntity(request)).thenReturn(warehouse);
        when(warehouseRepository.save(any(Warehouse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        warehouseService.createWarehouse(request);

        assertThat(warehouse.getActive()).isTrue();
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void createWarehouse_shouldThrow_whenCodeExists() {
        WarehouseCreateRequest request = new WarehouseCreateRequest();
        request.setName("Madurai Warehouse");
        request.setCode("CHN-01");

        when(warehouseRepository.existsByCode("CHN-01")).thenReturn(true);

        assertThatThrownBy(() -> warehouseService.createWarehouse(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("code");
    }

    @Test
    void getWarehouseById_shouldThrow_whenNotFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warehouseService.getWarehouseById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Warehouse not found");
    }

    @Test
    void deleteWarehouse_shouldSoftDelete() {
        Warehouse warehouse = Warehouse.builder().active(true).build();
        warehouse.setId(1L);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(Warehouse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        warehouseService.deleteWarehouse(1L);

        assertThat(warehouse.getActive()).isFalse();
    }
}
