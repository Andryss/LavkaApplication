package ru.yandex.yandexlavka.serivce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.exception.BadRequestException;
import ru.yandex.yandexlavka.objects.dto.CourierDto;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.GroupOrdersEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;
import ru.yandex.yandexlavka.objects.mapper.CourierMapper;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.GetCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;
import ru.yandex.yandexlavka.repository.CourierRepository;
import ru.yandex.yandexlavka.repository.GroupOrderRepository;
import ru.yandex.yandexlavka.repository.OffsetLimitPageable;
import ru.yandex.yandexlavka.repository.OrderRepository;
import ru.yandex.yandexlavka.serivce.rating.CourierRatingService;
import ru.yandex.yandexlavka.serivce.rating.CourierRatingService.CourierMetaInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CourierServiceImpl implements CourierService {

    private final CourierRatingService courierRatingService;

    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final GroupOrderRepository groupOrderRepository;

    private final CourierMapper courierMapper;

    @Autowired
    public CourierServiceImpl(CourierRatingService courierRatingService, CourierRepository courierRepository, OrderRepository orderRepository, GroupOrderRepository groupOrderRepository, CourierMapper courierMapper) {
        this.courierRatingService = courierRatingService;
        this.courierRepository = courierRepository;
        this.orderRepository = orderRepository;
        this.groupOrderRepository = groupOrderRepository;
        this.courierMapper = courierMapper;
    }

    @Transactional
    public CreateCouriersResponse addCouriers(CreateCourierRequest request) {
        List<CourierEntity> courierEntitiesToSave = request.getCouriers().stream()
                .map(courierMapper::mapCourierEntity)
                .toList();
        List<CourierDto> response = courierRepository.saveAll(courierEntitiesToSave).stream()
                .map(courierMapper::mapCourierDto)
                .toList();
        return new CreateCouriersResponse(response);
    }

    @Transactional(readOnly = true)
    public Optional<CourierDto> getCourierById(Long courierId) {
        return courierRepository.findById(courierId).map(courierMapper::mapCourierDto);
    }

    @Transactional(readOnly = true)
    public GetCouriersResponse getCourierRange(Integer offset, Integer limit) {
        Page<CourierEntity> courierEntityPage = courierRepository.findAll(OffsetLimitPageable.of(offset, limit));
        List<CourierDto> courierRange = courierEntityPage.stream()
                .map(courierMapper::mapCourierDto)
                .toList();
        return new GetCouriersResponse(courierRange, limit, offset);
    }

    @Transactional(readOnly = true)
    public GetCourierMetaInfoResponse getCourierMetaInfo(Long courierId, LocalDate startDate, LocalDate endDate) {
        // Check if courier exists
        Optional<CourierEntity> courierEntityOptional = courierRepository.findById(courierId);
        if (courierEntityOptional.isEmpty())
            throw BadRequestException.EMPTY;
        CourierEntity courierEntity = courierEntityOptional.get();

        // Fetch completed group orders
        List<GroupOrdersEntity> completedGroupOrders = groupOrderRepository.findAllByAssignedCourierAndCompleteTimeGreaterThanEqualAndCompleteTimeLessThan(
                courierEntity, startDate.atStartOfDay(), endDate.atStartOfDay()
        );
        // Fetch completed orders
        List<OrderEntity> completedOrders = orderRepository.findAllByAssignedGroupOrderIn(completedGroupOrders);

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
}
