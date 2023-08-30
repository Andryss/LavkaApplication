package ru.andryss.lavka.objects.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    @Temporal(TemporalType.TIMESTAMP)
    LocalDateTime completedTime;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_group_order_id")
    GroupOrdersEntity assignedGroupOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    RegionEntity regions;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "orders_delivery_hours",
            joinColumns = { @JoinColumn(name = "interval_id") },
            inverseJoinColumns = { @JoinColumn(name = "order_id") }
    )
    List<IntervalEntity> deliveryHours;
}
