package ru.yandex.yandexlavka.entities.couriers;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.entities.IntervalEntity;
import ru.yandex.yandexlavka.entities.RegionEntity;
import ru.yandex.yandexlavka.entities.orders.OrderEntity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "couriers")
public class CourierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "courier_id")
    Long courierId;

    @Column(name = "courier_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    CourierType courierType;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "couriers_regions",
            joinColumns = { @JoinColumn(name = "courier_id") },
            inverseJoinColumns = { @JoinColumn(name = "region_id") }
    )
    List<RegionEntity> regions;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(
            name = "couriers_working_hours",
            joinColumns = { @JoinColumn(name = "courier_id") },
            inverseJoinColumns = { @JoinColumn(name = "interval_id") }
    )
    List<IntervalEntity> workingHours;

    @OneToMany(mappedBy = "assignedCourierId")
    List<OrderEntity> assignedOrders;
}
