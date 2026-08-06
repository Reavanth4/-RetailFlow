package com.retailflow.purchaseservice.purchase.entity;

import com.retailflow.purchaseservice.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "supplier_returns")
public class SupplierReturn extends BaseEntity {

    @Column(name = "return_number", nullable = false, unique = true, length = 30)
    private String returnNumber;

    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @OneToMany(mappedBy = "supplierReturn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierReturnItem> items = new ArrayList<>();

    public void addItem(SupplierReturnItem item) {
        items.add(item);
        item.setSupplierReturn(this);
    }
}
