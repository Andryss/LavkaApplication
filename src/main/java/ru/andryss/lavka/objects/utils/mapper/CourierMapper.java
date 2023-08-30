package ru.andryss.lavka.objects.utils.mapper;

import ru.andryss.lavka.objects.dto.CourierDto;
import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCourierDto;

public interface CourierMapper {

    CourierEntity mapCourierEntity(CreateCourierDto createCourierDto);

    CourierDto mapCourierDto(CourierEntity courierEntity);

    GetCourierMetaInfoResponse mapCourierMetaInfoResponse(CourierEntity courierEntity);

}
