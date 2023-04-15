package ru.yandex.yandexlavka.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.controllers.validators.CreateCourierRequestValidator;
import ru.yandex.yandexlavka.entities.couriers.*;
import ru.yandex.yandexlavka.entities.exceptions.BadRequestException;
import ru.yandex.yandexlavka.entities.exceptions.NotFoundException;
import ru.yandex.yandexlavka.serivces.CourierService;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/couriers")
@Validated
public class CourierController {

    private final CreateCourierRequestValidator createCourierRequestValidator;
    private final CourierService courierService;

    @Autowired
    public CourierController(CreateCourierRequestValidator createCourierRequestValidator, CourierService courierService) {
        this.createCourierRequestValidator = createCourierRequestValidator;
        this.courierService = courierService;
    }

    @PostMapping
    ResponseEntity<CreateCouriersResponse> createCourier(
            @RequestBody @Valid CreateCourierRequest createCourierRequest,
            BindingResult bindingResult
    ) {
        createCourierRequestValidator.validate(createCourierRequest, bindingResult);
        if (bindingResult.hasErrors())
            throw new BadRequestException();
        CreateCouriersResponse response = courierService.addCouriers(createCourierRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courier_id}")
    ResponseEntity<CourierDto> getCourierById(
            @PathVariable("courier_id") Long courierId
    ) {
        Optional<CourierDto> courierById = courierService.getCourierById(courierId);
        if (courierById.isEmpty())
            throw new NotFoundException();
        return ResponseEntity.ok(courierById.get());
    }

    @GetMapping
    ResponseEntity<GetCouriersResponse> getCouriers(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer offset,
            @RequestParam(defaultValue = "1") @Positive Integer limit
    ) {
        GetCouriersResponse response = courierService.getCourierRange(offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/meta-info/{courier_id}")
    ResponseEntity<GetCourierMetaInfoResponse> getCourierMetaInfo(
            @PathVariable("courier_id") Long courierId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if (!startDate.isBefore(endDate))
            throw new BadRequestException();
        GetCourierMetaInfoResponse response = courierService.getCourierMetaInfo(courierId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
