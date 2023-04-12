package ru.yandex.yandexlavka.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "intervals")
public class IntervalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long intervalId;

    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIME)
    LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @Temporal(TemporalType.TIME)
    LocalTime endTime;
}
