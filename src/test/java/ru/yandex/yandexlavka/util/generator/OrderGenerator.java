package ru.yandex.yandexlavka.util.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderDto;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;

import java.util.List;

@Component
public class OrderGenerator {

    private final RegionGenerator regionGenerator;
    private final IntervalGenerator intervalGenerator;

    @Autowired
    public OrderGenerator(RegionGenerator regionGenerator, IntervalGenerator intervalGenerator) {
        this.regionGenerator = regionGenerator;
        this.intervalGenerator = intervalGenerator;
    }

    public Integer createCost() {
        return (int) (Math.random() * 100 + 1);
    }

    public Float createWeight() {
        return (float) (Math.random() * 40 + 1);
    }

    public CreateOrderDto createCreateOrderDto() {
        return new CreateOrderDto(
                createWeight(),
                regionGenerator.createRegionNumber(),
                intervalGenerator.createIntervals(),
                createCost()
        );
    }

    public List<CreateOrderDto> createCreateOrderDtoList() {
        return Generators.createListOf(
                (int) (Math.random() * 10 + 1),
                this::createCreateOrderDto
        );
    }

    public CreateOrderRequest createCreateOrderRequest() {
        return new CreateOrderRequest(
                createCreateOrderDtoList()
        );
    }

}
