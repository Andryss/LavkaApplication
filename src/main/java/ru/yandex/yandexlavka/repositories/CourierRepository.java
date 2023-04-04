package ru.yandex.yandexlavka.repositories;

import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entities.orders.CompleteOrder;
import ru.yandex.yandexlavka.entities.couriers.CourierDto;
import ru.yandex.yandexlavka.entities.couriers.CreateCourierDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class CourierRepository {

    // FIXME: change db
    private long courierId = 0;
    private final List<CourierDto> courierDtoList = new ArrayList<>(1024);

    public CourierDto addCourier(CourierDto courierDto) {
        courierDto.setCourierId(courierId++);
        courierDtoList.add(courierDto);
        return courierDto;
    }

    public List<CourierDto> addAllCouriers(List<CourierDto> courierDtoList) {
        List<CourierDto> courierDtos = new ArrayList<>(courierDtoList.size());
        courierDtoList.forEach(courierDto -> courierDtos.add(addCourier(courierDto)));
        return courierDtos;
    }

    public Optional<CourierDto> getCourierById(Long courierId) {
        return courierDtoList.stream().filter(courierDto -> courierDto.getCourierId().equals(courierId)).findAny();
    }

    public List<CourierDto> getCourierRange(int offset, int limit) {
        if (offset >= courierDtoList.size()) return Collections.emptyList();
        return courierDtoList.subList(offset, Math.min(offset + limit, courierDtoList.size()));
    }

    public boolean hasCourier(Long courierId) {
        return getCourierById(courierId).isPresent();
    }

    public boolean hasAllCouriers(List<CompleteOrder> completeOrders) {
        return completeOrders.stream().allMatch(completeOrder -> hasCourier(completeOrder.getCourierId()));
    }

}
