package com.retailflow.productservice.brand.entity;

import com.retailflow.productservice.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "brands",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_brand_name", columnNames = "name"),
                @UniqueConstraint(name = "uk_brand_code", columnNames = "brand_code")
        }
)
public class Brand extends BaseEntity {

    @Column(name = "brand_code", nullable = false, length = 20)
    private String brandCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(length = 255)
    private String website;

    @Column(nullable = false)
    private Boolean active = true;
}