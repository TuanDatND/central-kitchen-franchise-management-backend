package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
<<<<<<< feature/Login-Authorize
import java.util.List;
=======
>>>>>>> main

@Entity
@Table(name = "store_orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StoreOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @Column(nullable = false, unique = true)
    private String orderCode;

    @ManyToOne
    private Store store;

<<<<<<< feature/Login-Authorize
    @OneToMany(mappedBy = "storeOrder", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

=======
>>>>>>> main
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @Temporal(TemporalType.DATE)
    private Date deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StoreOrderStatus status;
}
