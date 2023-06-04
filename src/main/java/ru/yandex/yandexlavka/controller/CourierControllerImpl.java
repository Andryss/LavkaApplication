package ru.yandex.yandexlavka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.yandexlavka.controller.validator.CreateCourierRequestValidator;
import ru.yandex.yandexlavka.exception.BadRequestException;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.GetCourierResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.GetCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;
import ru.yandex.yandexlavka.serivce.CourierService;

import java.time.LocalDate;

@RestController
public class CourierControllerImpl implements CourierController {

    private final CreateCourierRequestValidator createCourierRequestValidator;
    private final CourierService courierService;

    @Autowired
    public CourierControllerImpl(CreateCourierRequestValidator createCourierRequestValidator, CourierService courierService) {
        this.createCourierRequestValidator = createCourierRequestValidator;
        this.courierService = courierService;
    }

    @Override
    public ResponseEntity<CreateCouriersResponse> createCourier(
            CreateCourierRequest createCourierRequest,
            BindingResult bindingResult
    ) {
        createCourierRequestValidator.validate(createCourierRequest, bindingResult);
        if (bindingResult.hasErrors())
            throw new BadRequestException(bindingResult.getAllErrors().toString());
        CreateCouriersResponse response = courierService.addCouriers(createCourierRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetCourierResponse> getCourierById(
            Long courierId
    ) {
        GetCourierResponse response = courierService.getCourierById(courierId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetCouriersResponse> getCouriers(
            Integer offset,
            Integer limit
    ) {
        GetCouriersResponse response = courierService.getCourierRange(offset, limit);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetCourierMetaInfoResponse> getCourierMetaInfo(
            Long courierId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (!startDate.isBefore(endDate))
            throw new BadRequestException(String.format("%s is after %s", startDate, endDate));
        GetCourierMetaInfoResponse response = courierService.getCourierMetaInfo(courierId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<OrderAssignResponse> couriersAssignments(
            LocalDate date,
            Long courierId
    ) {
        if (date == null) date = LocalDate.now();
        OrderAssignResponse response = courierService.getCouriersAssignments(date, courierId);
        return ResponseEntity.ok(response);
    }
}
