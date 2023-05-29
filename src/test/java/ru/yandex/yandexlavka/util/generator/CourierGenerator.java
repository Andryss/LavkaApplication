package ru.yandex.yandexlavka.util.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.dto.CourierType;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierDto;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierRequest;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourierGenerator {

    private final IntervalGenerator intervalGenerator;
    private final RegionGenerator regionGenerator;

    @Autowired
    public CourierGenerator(IntervalGenerator intervalGenerator, RegionGenerator regionGenerator) {
        this.intervalGenerator = intervalGenerator;
        this.regionGenerator = regionGenerator;
    }

    public CourierType createCourierType() {
        CourierType[] courierTypes = CourierType.values();
        return courierTypes[(int) (Math.random() * courierTypes.length)];
    }

    public CreateCourierDto createCreateCourierDto() {
        return new CreateCourierDto(
                createCourierType(),
                regionGenerator.createRegionNumberList(),
                intervalGenerator.createIntervals()
        );
    }

    public List<CreateCourierDto> createCreateCourierDtoList() {
        return Generators.createListOf(
                (int) (Math.random() * 10 + 1),
                this::createCreateCourierDto
        );
    }

    public CreateCourierRequest createCreateCourierRequest() {
        return new CreateCourierRequest(
                createCreateCourierDtoList()
        );
    }

}
