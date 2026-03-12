package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Integer deliveryId;

    @Column(name = "delivery_code", unique = true, nullable = false)
    private String deliveryCode;

    @Column(name = "driver_name", length = 100)
    private String driverName;

    @Column(name = "vehicle_plate", length = 50)
    private String vehiclePlate;

    @Column(name = "scheduled_date")
    private OffsetDateTime scheduledDate;

    @Column(name = "actual_start_date")
    private OffsetDateTime actualStartDate;

    @Column(name = "actual_end_date")
    private OffsetDateTime actualEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeliveryStatus status = DeliveryStatus.PLANNED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL)
    private List<ExportNote> exportNotes;
}
