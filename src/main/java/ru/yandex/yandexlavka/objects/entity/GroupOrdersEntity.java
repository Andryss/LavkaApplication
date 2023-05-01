package ru.yandex.yandexlavka.objects.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "group_orders")
public class GroupOrdersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "group_id")
    Long groupId;

    @Column(name = "assigned_date")
    @Temporal(TemporalType.DATE)
    LocalDate assignedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_courier_id")
    CourierEntity assignedCourier;

    @OneToMany(mappedBy = "assignedGroupOrder", fetch = FetchType.LAZY)
    List<OrderEntity> orders;
}
