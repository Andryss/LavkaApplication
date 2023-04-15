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
                regionMapper.mapRegionEntityList(createCourierDto.getRegions()),
                intervalMapper.mapIntervalEntityList(createCourierDto.getWorkingHours()),
                null
        );
    }

    public CourierEntity mapCourierEntity(CourierDto courierDto) {
        return new CourierEntity(
                courierDto.getCourierId(),
                courierDto.getCourierType(),
                regionMapper.mapRegionEntityList(courierDto.getRegions()),
                intervalMapper.mapIntervalEntityList(courierDto.getWorkingHours()),
                null
        );
    }

    public CourierDto mapCourierDto(CourierEntity courierEntity) {
        return new CourierDto(
                courierEntity.getCourierId(),
                courierEntity.getCourierType(),
                regionMapper.mapRegionNumberList(courierEntity.getRegions()),
                intervalMapper.mapIntervalStringList(courierEntity.getWorkingHours())
        );
    }

    public GetCourierMetaInfoResponse mapCourierMetaInfoResponse(CourierEntity courierEntity) {
        return new GetCourierMetaInfoResponse(
                courierEntity.getCourierId(),
                courierEntity.getCourierType(),
                regionMapper.mapRegionNumberList(courierEntity.getRegions()),
                intervalMapper.mapIntervalStringList(courierEntity.getWorkingHours()),
                null,
                null
        );
    }

}
