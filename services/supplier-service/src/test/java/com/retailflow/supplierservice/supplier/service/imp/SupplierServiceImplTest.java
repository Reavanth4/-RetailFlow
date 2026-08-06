package com.retailflow.supplierservice.supplier.service.imp;

import com.retailflow.supplierservice.common.exception.DuplicateResourceException;
import com.retailflow.supplierservice.common.exception.ResourceNotFoundException;
import com.retailflow.supplierservice.supplier.dto.request.SupplierCreateRequest;
import com.retailflow.supplierservice.supplier.entity.Supplier;
import com.retailflow.supplierservice.supplier.mapper.SupplierMapper;
import com.retailflow.supplierservice.supplier.repository.SupplierRepository;
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
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    private SupplierServiceImpl supplierService;

    @BeforeEach
    void setUp() {
        supplierService = new SupplierServiceImpl(supplierRepository, supplierMapper);
    }

    @Test
    void createSupplier_shouldSaveActiveSupplier() {
        SupplierCreateRequest request = new SupplierCreateRequest();
        request.setName("Gold Traders");
        request.setPhone("9876543210");

        Supplier supplier = Supplier.builder().name("Gold Traders").build();

        when(supplierMapper.toEntity(request)).thenReturn(supplier);
        when(supplierRepository.save(any(Supplier.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        supplierService.createSupplier(request);

        assertThat(supplier.getActive()).isTrue();
        verify(supplierRepository).save(supplier);
    }

    @Test
    void createSupplier_shouldThrow_whenNameExists() {
        SupplierCreateRequest request = new SupplierCreateRequest();
        request.setName("Gold Traders");

        when(supplierRepository.existsByName("Gold Traders")).thenReturn(true);

        assertThatThrownBy(() -> supplierService.createSupplier(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getSupplierById_shouldThrow_whenNotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.getSupplierById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found");
    }
}
