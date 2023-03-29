package ru.yandex.yandexlavka.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.entities.*;
import ru.yandex.yandexlavka.entities.dto.CourierDto;
import ru.yandex.yandexlavka.serivces.CourierService;

import java.util.List;
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
    ResponseEntity<?> createCourier(
            @RequestBody @Valid CreateCourierRequest createCourierRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(new BadRequestResponse());
        CreateCouriersResponse response = courierService.addCouriers(createCourierRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courier_id}")
    ResponseEntity<?> getCourierById(
            @PathVariable(name = "courier_id") Long courierId
    ) {
        Optional<CourierDto> courierById = courierService.getCourierById(courierId);
        if (courierById.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse());
        return ResponseEntity.ok(courierById.get());
    }

    @GetMapping
    ResponseEntity<?> getCouriers(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "1") Integer limit
    ) {
        if (offset < 0 || limit < 1)
            return ResponseEntity.badRequest().body(new BadRequestResponse());
        GetCouriersResponse response = courierService.getCourierRange(offset, limit);
        return ResponseEntity.ok(response);
    }
}
