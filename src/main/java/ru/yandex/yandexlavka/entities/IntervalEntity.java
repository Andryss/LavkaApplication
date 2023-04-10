package ru.yandex.yandexlavka.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

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
    Time startTime;

    @Column(name = "end_time", nullable = false)
    @Temporal(TemporalType.TIME)
    Time endTime;
}
