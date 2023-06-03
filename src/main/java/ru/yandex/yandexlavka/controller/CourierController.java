package ru.yandex.yandexlavka.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.GetCourierResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.GetCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;

import java.time.LocalDate;

@RequestMapping("/couriers")
@Validated
public interface CourierController {

    /*
    TODO: rewrite the javadoc
     */
    /**
     * Loads couriers into the system
     * @param createCourierRequest info about couriers to load
     * @param bindingResult object containing errors during parsing and validating couriers info
     * @return added couriers with assigned IDs
     */
    @PostMapping
    ResponseEntity<CreateCouriersResponse> createCourier(
            @RequestBody @Valid CreateCourierRequest createCourierRequest,
            BindingResult bindingResult
    );

    /**
     * Returns info about courier
     * @param courierId courier id to get info about
     * @return courier info
     */
    @GetMapping("/{courier_id}")
    ResponseEntity<GetCourierResponse> getCourierById(
            @PathVariable("courier_id") Long courierId
    );

    /**
     * Returns info about all couriers
     * @param offset number of couriers to skip from start, default is 0 (start position)
     * @param limit numbers of couriers to return, default is 1 (slice size)
     * @return list of couriers info
     */
    @GetMapping
    ResponseEntity<GetCouriersResponse> getCouriers(
            @RequestParam(name = "offset", defaultValue = "0") @PositiveOrZero Integer offset,
            @RequestParam(name = "limit", defaultValue = "1") @Positive Integer limit
    );

    /**
     * Counts and returns rating of the courier
     * @param courierId courier identifier to know all about (joke, just rating, no more...)
     * @param startDate start of the interval from which to count rating
     * @param endDate end of that interval
     * @return courier meta-info during given interval
     */
    @GetMapping("/meta-info/{courier_id}")
    ResponseEntity<GetCourierMetaInfoResponse> getCourierMetaInfo(
            @PathVariable("courier_id") Long courierId,
            @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    );

    /**
     * Returns group orders assigned to the courier
     * @param date returns groups assigned at that date
     * @param courierId courier identifier to know about
     * @return list of group orders assigned to the courier at some date
     */
    @GetMapping("/assignments")
    ResponseEntity<OrderAssignResponse> couriersAssignments(
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("courier_id") Long courierId
    );

}
