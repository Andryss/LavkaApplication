package ru.yandex.yandexlavka.util.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegionGenerator {

    public Integer createRegionNumber() {
        return (int) (Math.random() * 1_000 + 1);
    }

    public List<Integer> createRegionNumberList() {
        return new ArrayList<>(
                Generators.createSetOf(
                        (int) (Math.random() * 20 + 1),
                        this::createRegionNumber
                )
        );
    }

}
