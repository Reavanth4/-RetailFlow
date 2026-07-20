package com.retailflow.productservice.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter

public abstract class BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_at" , nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "created_by", length = 100)
    private String createdBy;
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    @Version
    private Long version;
    @PrePersist
    protected void onCreate()
    {
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();

    }
    protected void onUpdate()
    {
        updatedAt=LocalDateTime.now();
    }
}
