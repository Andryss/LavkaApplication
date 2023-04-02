package ru.yandex.yandexlavka.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.entities.couriers.CreateCourierRequest;
import ru.yandex.yandexlavka.entities.couriers.CourierDto;
import ru.yandex.yandexlavka.entities.couriers.CreateCouriersResponse;
import ru.yandex.yandexlavka.entities.couriers.GetCouriersResponse;
import ru.yandex.yandexlavka.entities.exceptions.BadRequestException;
import ru.yandex.yandexlavka.entities.exceptions.BadRequestResponse;
import ru.yandex.yandexlavka.entities.exceptions.NotFoundException;
import ru.yandex.yandexlavka.entities.exceptions.NotFoundResponse;
import ru.yandex.yandexlavka.serivces.CourierService;

import java.util.Optional;

@RestController
@RequestMapping("/couriers")
public class CourierController {

    private final CourierService courierService;

    @Autowired
    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @PostMapping
    ResponseEntity<CreateCouriersResponse> createCourier(
            @RequestBody @Valid CreateCourierRequest createCourierRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            throw new BadRequestException();
        CreateCouriersResponse response = courierService.addCouriers(createCourierRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courier_id}")
    ResponseEntity<CourierDto> getCourierById(
            @PathVariable(name = "courier_id") Long courierId
    ) {
        Optional<CourierDto> courierById = courierService.getCourierById(courierId);
        if (courierById.isEmpty())
            throw new NotFoundException();
        return ResponseEntity.ok(courierById.get());
    }

    @GetMapping
    ResponseEntity<GetCouriersResponse> getCouriers(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "1") Integer limit
    ) {
        if (offset < 0 || limit < 1)
            throw new BadRequestException();
        GetCouriersResponse response = courierService.getCourierRange(offset, limit);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler({ BadRequestException.class })
    ResponseEntity<BadRequestResponse> handleBadRequest() {
        return ResponseEntity.badRequest().body(new BadRequestResponse());
    }

    @ExceptionHandler({ NotFoundException.class })
    ResponseEntity<NotFoundResponse> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse());
    }
}
