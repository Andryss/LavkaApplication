package ru.yandex.yandexlavka.objects.utils.mapper;

import ru.yandex.yandexlavka.objects.dto.CourierDto;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierDto;
import ru.yandex.yandexlavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;

public interface CourierMapper {

    CourierEntity mapCourierEntity(CreateCourierDto createCourierDto);

    CourierDto mapCourierDto(CourierEntity courierEntity);

    GetCourierMetaInfoResponse mapCourierMetaInfoResponse(CourierEntity courierEntity);

}
