package ru.yandex.yandexlavka.objects.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.yandexlavka.objects.dto.CourierType;

import java.util.List;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "couriers_regions",
            joinColumns = { @JoinColumn(name = "courier_id") },
            inverseJoinColumns = { @JoinColumn(name = "region_id") }
    )
    List<RegionEntity> regions;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "couriers_working_hours",
            joinColumns = { @JoinColumn(name = "courier_id") },
            inverseJoinColumns = { @JoinColumn(name = "interval_id") }
    )
    List<IntervalEntity> workingHours;

    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assignedCourier")
    Set<GroupOrdersEntity> assignedGroupOrders;
}
