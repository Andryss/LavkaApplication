package ru.yandex.yandexlavka.serivce;

import ru.yandex.yandexlavka.objects.dto.CourierDto;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.GetCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;

import java.time.LocalDate;
import java.util.Optional;

public interface CourierService {

    CreateCouriersResponse addCouriers(CreateCourierRequest request);

    Optional<CourierDto> getCourierById(Long courierId);

    GetCouriersResponse getCourierRange(Integer offset, Integer limit);

    GetCourierMetaInfoResponse getCourierMetaInfo(Long courierId, LocalDate startDate, LocalDate endDate);

    OrderAssignResponse getCouriersAssignments(LocalDate date, Long courierId);

}
