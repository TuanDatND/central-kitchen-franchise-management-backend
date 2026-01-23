package com.CocOgreen.CenFra.MS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public abstract class BaseEntity {
    @CreatedDate
    @Column(updatable = false, columnDefinition = "DATETIME2")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(columnDefinition = "DATETIME2")
    private LocalDateTime modifiedAt;

    @CreatedBy
    @Column(updatable = false)
    private Integer createdBy;

    @LastModifiedBy
    private Integer modifiedBy;

    @Version
    @Column(name = "ver")
    private Integer version;
}
