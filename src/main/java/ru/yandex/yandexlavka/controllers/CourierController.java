package ru.yandex.yandexlavka.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.entities.CreateCourierRequest;
import ru.yandex.yandexlavka.entities.CreateCouriersResponse;
import ru.yandex.yandexlavka.entities.GetCouriersResponse;
import ru.yandex.yandexlavka.entities.dto.CourierDto;

@RestController
@RequestMapping("/couriers")
public class CourierController {

    @PostMapping
    ResponseEntity<CreateCouriersResponse> createCourier(
            @RequestBody @Valid CreateCourierRequest createCourierRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().build();
        // TODO: implement
        // TODO: what BadRequestResponse?
        return null;
    }

    @GetMapping("/{courier_id}")
    ResponseEntity<CourierDto[]> getCourierById(
            @PathVariable(name = "courier_id") Long courierId
    ) {
        // TODO: implement
        return null;
    }

    @GetMapping
    ResponseEntity<GetCouriersResponse> getCouriers(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "1") Integer limit
    ) {
        // TODO: implement
        return null;
    }
}
