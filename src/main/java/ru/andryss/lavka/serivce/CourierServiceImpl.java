package ru.andryss.lavka.serivce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andryss.lavka.exception.BadRequestException;
import ru.andryss.lavka.exception.NotFoundException;
import ru.andryss.lavka.objects.dto.CourierDto;
import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.entity.GroupOrdersEntity;
import ru.andryss.lavka.objects.mapping.assign.order.CouriersGroupOrders;
import ru.andryss.lavka.objects.mapping.assign.order.GroupOrders;
import ru.andryss.lavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.andryss.lavka.objects.mapping.get.courier.GetCourierResponse;
import ru.andryss.lavka.objects.mapping.get.courier.GetCouriersResponse;
import ru.andryss.lavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;
import ru.andryss.lavka.objects.utils.mapper.CourierMapper;
import ru.andryss.lavka.objects.utils.mapper.GroupOrdersMapper;
import ru.andryss.lavka.repository.CourierRepository;
import ru.andryss.lavka.repository.GroupOrderRepository;
import ru.andryss.lavka.repository.OffsetLimitPageable;
import ru.andryss.lavka.objects.entity.OrderEntity;
import ru.andryss.lavka.serivce.rating.CourierRatingService;
import ru.andryss.lavka.serivce.rating.CourierRatingService.CourierMetaInfo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.andryss.lavka.objects.utils.IntervalEntityUtils.hasIntersections;

@Service
public class CourierServiceImpl implements CourierService {

    private final CourierRatingService courierRatingService;

    private final CourierRepository courierRepository;
    private final GroupOrderRepository groupOrderRepository;
    private final GroupOrdersMapper groupOrdersMapper;

    private final CourierMapper courierMapper;

    @Autowired
    public CourierServiceImpl(CourierRatingService courierRatingService, CourierRepository courierRepository, GroupOrderRepository groupOrderRepository, GroupOrdersMapper groupOrdersMapper, CourierMapper courierMapper) {
        this.courierRatingService = courierRatingService;
        this.courierRepository = courierRepository;
        this.groupOrderRepository = groupOrderRepository;
        this.groupOrdersMapper = groupOrdersMapper;
        this.courierMapper = courierMapper;
    }

    @Override
    @Transactional
    public CreateCouriersResponse addCouriers(CreateCourierRequest request) {
        List<CourierEntity> courierEntitiesToSave = request.getCouriers().stream()
                .map(courierMapper::mapCourierEntity)
                .peek(this::courierEntityHasNoIntersectingIntervals)
                .toList();
        List<CourierDto> response = courierRepository.saveAll(courierEntitiesToSave).stream()
                .map(courierMapper::mapCourierDto)
                .toList();
        return new CreateCouriersResponse(response);
    }

    private void courierEntityHasNoIntersectingIntervals(CourierEntity courierEntity) {
        if (hasIntersections(courierEntity.getWorkingHours()))
            throw new BadRequestException(String.format("working hours has intersections %s", courierEntity.getWorkingHours()));
    }

    @Override
    @Transactional(readOnly = true)
    public GetCourierResponse getCourierById(Long courierId) {
        Optional<CourierDto> courierDtoOptional = courierRepository.findById(courierId).map(courierMapper::mapCourierDto);
        if (courierDtoOptional.isEmpty())
            throw new NotFoundException(String.format("no courier with id %d", courierId));
        return new GetCourierResponse(courierDtoOptional.get());
    }

    @Transactional(readOnly = true)
    CourierEntity getCourierEntityById(Long courierId) {
        Optional<CourierEntity> courierEntityOptional = courierRepository.findById(courierId);
        if (courierEntityOptional.isEmpty())
            throw new BadRequestException(String.format("no courier with id %d", courierId));
        return courierEntityOptional.get();
    }

    @Override
    @Transactional(readOnly = true)
    public GetCouriersResponse getCourierRange(Integer offset, Integer limit) {
        Page<CourierEntity> courierEntityPage = courierRepository.findAll(OffsetLimitPageable.of(offset, limit));
        List<CourierDto> courierRange = courierEntityPage.stream()
                .map(courierMapper::mapCourierDto)
                .toList();
        return new GetCouriersResponse(courierRange, limit, offset);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCourierMetaInfoResponse getCourierMetaInfo(Long courierId, LocalDate startDate, LocalDate endDate) {
        // Check if courier exists
        CourierEntity courierEntity = getCourierEntityById(courierId);

        // Fetch completed orders
        List<OrderEntity> completedOrders = courierEntity.getAssignedGroupOrders().stream()
                .flatMap(groupOrders -> groupOrders.getOrders().stream())
                .filter(orderEntity -> orderEntity.getCompletedTime() != null)
                .filter(orderEntity -> !orderEntity.getCompletedTime().isBefore(startDate.atStartOfDay()) && orderEntity.getCompletedTime().isBefore(endDate.atStartOfDay()))
                .toList();

        GetCourierMetaInfoResponse response = courierMapper.mapCourierMetaInfoResponse(courierEntity);

        // Fill response
        if (!completedOrders.isEmpty()) {
            CourierMetaInfo courierMetaInfo = courierRatingService.getCourierMetaInfo(
                    courierEntity, startDate, endDate, completedOrders
            );
            response.setRating(courierMetaInfo.getRating());
            response.setEarnings(courierMetaInfo.getEarnings());
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderAssignResponse getCouriersAssignments(LocalDate date, Long courierId) {
        // Check if courier exists
        CourierEntity courierEntity = getCourierEntityById(courierId);

        // Get assigned group orders
        List<GroupOrdersEntity> assignedGroupOrdersEntities = groupOrderRepository.findAllByAssignedCourierAndAssignedDateGreaterThanEqualAndAssignedDateLessThan(
                courierEntity, date, date.plusDays(1)
        );

        List<GroupOrders> assignedGroupOrders = groupOrdersMapper.mapGroupOrdersList(assignedGroupOrdersEntities);

        return new OrderAssignResponse(
                date.toString(),
                Collections.singletonList(new CouriersGroupOrders(
                        courierId,
                        assignedGroupOrders
                ))
        );
    }
}
