package ru.yandex.yandexlavka.serivces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.entities.couriers.CreateCourierRequest;
import ru.yandex.yandexlavka.entities.couriers.CreateCouriersResponse;
import ru.yandex.yandexlavka.entities.couriers.GetCouriersResponse;
import ru.yandex.yandexlavka.entities.couriers.CourierDto;
import ru.yandex.yandexlavka.entities.couriers.CreateCourierDto;
import ru.yandex.yandexlavka.repositories.CourierRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class CourierService {

    private final CourierRepository courierRepository;

    @Autowired
    public CourierService(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    public CreateCouriersResponse addCouriers(CreateCourierRequest request) {
        List<CreateCourierDto> createCourierDtoList = request.getCouriers();
        List<CourierDto> courierDtoList = createCourierDtoList.stream().map(CourierDto::new).toList();
        List<CourierDto> response = courierRepository.addAllCouriers(courierDtoList);
        return new CreateCouriersResponse(response);
    }

    public Optional<CourierDto> getCourierById(Long courierId) {
        return courierRepository.getCourierById(courierId);
    }

    public GetCouriersResponse getCourierRange(Integer offset, Integer limit) {
        List<CourierDto> courierRange = courierRepository.getCourierRange(offset, limit);
        return new GetCouriersResponse(courierRange, limit, offset);
    }
}
