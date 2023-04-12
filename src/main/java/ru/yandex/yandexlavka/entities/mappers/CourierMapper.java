package ru.yandex.yandexlavka.entities.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.entities.couriers.CourierDto;
import ru.yandex.yandexlavka.entities.couriers.CourierEntity;
import ru.yandex.yandexlavka.entities.couriers.CreateCourierDto;
import ru.yandex.yandexlavka.entities.couriers.GetCourierMetaInfoResponse;

@Component
public class CourierMapper {

    private final IntervalMapper intervalMapper;
    private final RegionMapper regionMapper;

    public CourierMapper(IntervalMapper intervalMapper, RegionMapper regionMapper) {
        this.intervalMapper = intervalMapper;
        this.regionMapper = regionMapper;
    }

    public CourierDto mapCourierDto(CreateCourierDto createCourierDto) {
        return new CourierDto(
                null,
                createCourierDto.getCourierType(),
                createCourierDto.getRegions(),
                createCourierDto.getWorkingHours()
        );
    }

    public CourierEntity mapCourierEntity(CreateCourierDto createCourierDto) {
        return new CourierEntity(
                null,
                createCourierDto.getCourierType(),
                createCourierDto.getRegions().stream().map(regionMapper::mapRegionEntity).toList(),
                createCourierDto.getWorkingHours().stream().map(intervalMapper::mapIntervalEntity).toList(),
                null
        );
    }

    public CourierEntity mapCourierEntity(CourierDto courierDto) {
        return new CourierEntity(
                courierDto.getCourierId(),
                courierDto.getCourierType(),
                courierDto.getRegions().stream().map(regionMapper::mapRegionEntity).toList(),
                courierDto.getWorkingHours().stream().map(intervalMapper::mapIntervalEntity).toList(),
                null
        );
    }

    public CourierDto mapCourierDto(CourierEntity courierEntity) {
        return new CourierDto(
                courierEntity.getCourierId(),
                courierEntity.getCourierType(),
                courierEntity.getRegions().stream().map(regionMapper::mapRegionNumber).toList(),
                courierEntity.getWorkingHours().stream().map(intervalMapper::mapIntervalString).toList()
        );
    }

    public GetCourierMetaInfoResponse mapCourierMetaInfoResponse(CourierEntity courierEntity) {
        return new GetCourierMetaInfoResponse(
                courierEntity.getCourierId(),
                courierEntity.getCourierType(),
                courierEntity.getRegions().stream().map(regionMapper::mapRegionNumber).toList(),
                courierEntity.getWorkingHours().stream().map(intervalMapper::mapIntervalString).toList(),
                null,
                null
        );
    }

}
