package ru.yandex.yandexlavka.entities.orders;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.entities.IntervalEntity;
import ru.yandex.yandexlavka.entities.RegionEntity;
import ru.yandex.yandexlavka.entities.couriers.CourierEntity;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_id")
    Long orderId;

    @Column(name = "weight", nullable = false)
    Float weight;

    @Column(name = "cost", nullable = false)
    Integer cost;

    @Column(name = "completed_time")
    Date completedTime;

    @ManyToOne
    @JoinColumn(name = "assigned_courier_id")
    CourierEntity assignedCourierId;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "regions")
    RegionEntity regions;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(
            name = "orders_delivery_hours",
            joinColumns = { @JoinColumn(name = "interval_id") },
            inverseJoinColumns = { @JoinColumn(name = "order_id") }
    )
    List<IntervalEntity> deliveryHours;
}