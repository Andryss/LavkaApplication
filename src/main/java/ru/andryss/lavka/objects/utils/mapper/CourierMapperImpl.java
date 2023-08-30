package ru.andryss.lavka.objects.utils.mapper;

import org.springframework.stereotype.Component;
import ru.andryss.lavka.objects.dto.CourierDto;
import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCourierDto;

@Component
public class CourierMapperImpl implements CourierMapper {

    private final IntervalMapper intervalMapper;
    private final RegionMapper regionMapper;

    public CourierMapperImpl(IntervalMapper intervalMapper, RegionMapper regionMapper) {
        this.intervalMapper = intervalMapper;
        this.regionMapper = regionMapper;
    }

    @Override
    public CourierEntity mapCourierEntity(CreateCourierDto createCourierDto) {
        return new CourierEntity(
                null,
                createCourierDto.getCourierType(),
                regionMapper.mapRegionEntityList(createCourierDto.getRegions()),
                intervalMapper.mapIntervalEntityList(createCourierDto.getWorkingHours()),
                null
        );
    }

    @Override
    public CourierDto mapCourierDto(CourierEntity courierEntity) {
        return new CourierDto(
                courierEntity.getCourierId(),
                courierEntity.getCourierType(),
                regionMapper.mapRegionNumberList(courierEntity.getRegions()),
                intervalMapper.mapIntervalStringList(courierEntity.getWorkingHours())
        );
    }

    @Override
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
