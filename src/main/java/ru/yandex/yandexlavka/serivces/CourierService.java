package ru.yandex.yandexlavka.serivces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.entities.couriers.*;
import ru.yandex.yandexlavka.entities.mappers.CourierMapper;
import ru.yandex.yandexlavka.repositories.CourierRepository;
import ru.yandex.yandexlavka.repositories.OffsetLimitPageable;

import java.util.List;
import java.util.Optional;

@Service
public class CourierService {

    private final CourierRepository courierRepository;
    private final CourierMapper courierMapper;

    @Autowired
    public CourierService(CourierRepository courierRepository, CourierMapper courierMapper) {
        this.courierRepository = courierRepository;
        this.courierMapper = courierMapper;
    }

    public CreateCouriersResponse addCouriers(CreateCourierRequest request) {
        List<CourierEntity> courierEntitiesToSave = request.getCouriers().stream()
                .map(courierMapper::mapCourierEntity)
                .toList();
        List<CourierDto> response = courierRepository.saveAll(courierEntitiesToSave).stream()
                .map(courierMapper::mapCourierDto)
                .toList();
        return new CreateCouriersResponse(response);
    }

    public Optional<CourierDto> getCourierById(Long courierId) {
        return courierRepository.findById(courierId).map(courierMapper::mapCourierDto);
    }

    public GetCouriersResponse getCourierRange(Integer offset, Integer limit) {
        Page<CourierEntity> courierEntityPage = courierRepository.findAll(OffsetLimitPageable.of(offset, limit));
        List<CourierDto> courierRange = courierEntityPage.stream()
                .map(courierMapper::mapCourierDto)
                .toList();
        return new GetCouriersResponse(courierRange, limit, offset);
    }
}
