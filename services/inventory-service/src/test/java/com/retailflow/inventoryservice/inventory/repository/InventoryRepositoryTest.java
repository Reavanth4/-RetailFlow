package com.retailflow.inventoryservice.inventory.repository;

import com.retailflow.inventoryservice.inventory.entity.Inventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    private Inventory inventory(Long productId, Long warehouseId) {
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setWarehouseId(warehouseId);
        inventory.setQuantity(100);
        inventory.setReservedQuantity(0);
        inventory.setActive(true);
        return inventory;
    }

    @Test
    void productAndWarehouseCombination_shouldBeUnique() {
        inventoryRepository.save(inventory(1L, 1L));

        assertThatThrownBy(() -> inventoryRepository.save(inventory(1L, 1L)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void sameProductInDifferentWarehouses_shouldBeAllowed() {
        inventoryRepository.save(inventory(1L, 1L));
        inventoryRepository.save(inventory(1L, 2L));

        assertThat(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L)).isPresent();
        assertThat(inventoryRepository.findByProductIdAndWarehouseId(1L, 2L)).isPresent();
    }
}
