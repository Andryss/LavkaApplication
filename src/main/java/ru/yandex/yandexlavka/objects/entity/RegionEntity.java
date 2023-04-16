package ru.yandex.yandexlavka.objects.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "regions")
public class RegionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "region_id")
    Long regionId;

    @Column(name = "region_number", nullable = false, unique = true)
    Integer regionNumber;
}
