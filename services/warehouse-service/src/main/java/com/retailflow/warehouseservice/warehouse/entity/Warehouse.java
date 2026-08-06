package com.retailflow.warehouseservice.warehouse.entity;

import com.retailflow.warehouseservice.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "warehouses",
        uniqueConstraints = {
                @jakarta.persistence.UniqueConstraint(name = "uk_warehouse_code", columnNames = "code"),
                @jakarta.persistence.UniqueConstraint(name = "uk_warehouse_name", columnNames = "name")
        }
)
public class Warehouse extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "location", length = 255)
    private String location;

    @Column(nullable = false)
    private Boolean active = true;
}
