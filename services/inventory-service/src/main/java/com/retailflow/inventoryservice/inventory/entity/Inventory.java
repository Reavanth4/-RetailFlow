package com.retailflow.inventoryservice.inventory.entity;

import com.retailflow.inventoryservice.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "inventories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product_warehouse",
                        columnNames = {"product_id", "warehouse_id"}
                )
        }
)
public class Inventory extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(nullable = false)
    private Boolean active = true;

    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
}
