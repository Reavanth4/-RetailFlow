package com.retailflow.salesservice.sale.entity;

import com.retailflow.salesservice.common.entity.BaseEntity;
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
@Table(name = "returns")
public class SaleReturn extends BaseEntity {

    @Column(name = "sale_id", nullable = false)
    private Long saleId;

    @Column(name = "return_number", nullable = false, unique = true, length = 30)
    private String returnNumber;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(length = 500)
    private String reason;

    @OneToMany(mappedBy = "saleReturn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnItem> items = new ArrayList<>();

    public void addItem(ReturnItem item) {
        items.add(item);
        item.setSaleReturn(this);
    }
}
