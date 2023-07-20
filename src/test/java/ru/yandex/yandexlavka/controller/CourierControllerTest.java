package ru.yandex.yandexlavka.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.yandexlavka.objects.dto.CourierDto;
import ru.yandex.yandexlavka.objects.dto.CourierType;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierDto;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.GetCourierResponse;
import ru.yandex.yandexlavka.objects.mapping.get.courier.GetCouriersResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class CourierControllerTest {
    
    @Autowired
    CourierUtil courierUtil;

    @Autowired
    MockMvc mockMvc;


    @Test
    @DirtiesContext
    void whenCreateCourier_thenReturnEqualResponse() throws Exception {
        // given
        CreateCourierDto courierDto = new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00"));
        CreateCourierRequest request = new CreateCourierRequest(List.of(courierDto));

        // when
        CreateCouriersResponse response = courierUtil.createCouriers(request);

        // then
        List<CourierDto> createdCouriersDto = response.getCouriers();
        assertThat(createdCouriersDto, is(iterableWithSize(1)));

        CourierDto createdCourierDto = createdCouriersDto.get(0);
        assertThat(createdCourierDto.getCourierId(), is(notNullValue()));
        assertThat(createdCourierDto.getCourierType(), is(equalTo(CourierType.FOOT)));
        assertThat(createdCourierDto.getRegions(), is(equalTo(List.of(1, 2, 3))));
        assertThat(createdCourierDto.getWorkingHours(), is(equalTo(List.of("10:00-12:00", "13:00-17:00"))));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCreateCourierRequests")
    void whenCreateCourierWithInvalidData_thenReturnBadRequest(CreateCourierRequest request) throws Exception {
        // when + then
        courierUtil.createCouriersReturnResult(request)
                .andExpect(status().isBadRequest());
    }

    private static CreateCourierRequest[] provideInvalidCreateCourierRequests() {
        return new CreateCourierRequest[]{
                // invalid CreateCourierRequest
                null,
                new CreateCourierRequest(null),
                new CreateCourierRequest(List.of()),
                // invalid CreateCourierDto
                new CreateCourierRequest(Collections.singletonList(null)),
                // invalid CreateCourierDto courierType
                new CreateCourierRequest(List.of(new CreateCourierDto(null, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00")))),
                // invalid CreateCourierDto regions
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, null, List.of("10:00-12:00", "13:00-17:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(), List.of("10:00-12:00", "13:00-17:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, Collections.singletonList(null), List.of("10:00-12:00", "13:00-17:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(0, 2, 3), List.of("10:00-12:00", "13:00-17:00")))),
                // invalid CreateCourierDto workingHours
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), null))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of()))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), Collections.singletonList(null)))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("invalid")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("010:00-12:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("50:00-51:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:60-12:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10::00-12:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("1000-12:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00--12:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-09:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("00:00-00:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("23:59-00:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-14:00", "13:00-17:00")))),
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-11:00", "11:00-12:00"))))
        };
    }


    @Test
    @DirtiesContext
    void whenGetCourierById_thenReturnEqualOne() throws Exception {
        // given
        CreateCouriersResponse createCouriersResponse = courierUtil.createCouriers(
                new CreateCourierRequest(List.of(new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00"))))
        );
        CourierDto courierDto = createCouriersResponse.getCouriers().get(0);
        Long courierId = courierDto.getCourierId();

        // when
        GetCourierResponse response = courierUtil.getCourierById(courierId);

        // then
        CourierDto foundCourierDto = response.getCourier();
        assertThat(foundCourierDto, is(equalTo(courierDto)));
    }

    @Test
    void whenGetCourierWithInvalidId_thenReturnBadRequest() throws Exception {
        // when + then
        mockMvc.perform(get("/couriers/invalidId"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetCourierByNonExistingId_thenReturnNotFound() throws Exception {
        // when + then
        courierUtil.getCourierByIdReturnResult(Long.MIN_VALUE)
                .andExpect(status().isNotFound());
    }


    @Test
    @DirtiesContext
    void whenGetCouriers_thenReturnAllEqualOnes() throws Exception {
        // given
        CreateCouriersResponse createCouriersResponse = courierUtil.createCouriers(
                new CreateCourierRequest(List.of(
                        new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00")),
                        new CreateCourierDto(CourierType.BIKE, List.of(2, 3, 5), List.of("08:00-08:50")),
                        new CreateCourierDto(CourierType.AUTO, List.of(5, 6), List.of("10:00-12:00", "13:00-15:00", "17:30-20:00"))
                ))
        );
        List<CourierDto> createdCourierDtos = createCouriersResponse.getCouriers();

        // when
        GetCouriersResponse response = courierUtil.getCouriers(0, 3);

        // then
        List<CourierDto> foundCourierDtos = response.getCouriers();
        assertThat(foundCourierDtos, is(iterableWithSize(3)));
        assertThat(foundCourierDtos, is(equalTo(createdCourierDtos)));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidGetCouriersParameters")
    void whenGetCouriersWithInvalidParameters_thenReturnBadRequest(Integer offset, Integer limit) throws Exception {
        // when + then
        courierUtil.getCouriersReturnResult(offset, limit)
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidGetCouriersParameters() {
        return Stream.of(
                // invalid offset
                Arguments.of(-1, null),
                Arguments.of(-1, 1),
                Arguments.of(-1, Integer.MAX_VALUE),
                Arguments.of(Integer.MIN_VALUE, null),
                // invalid limit
                Arguments.of(null, 0),
                Arguments.of(0, 0),
                Arguments.of(Integer.MAX_VALUE, 0),
                Arguments.of(null, -1),
                Arguments.of(null, Integer.MIN_VALUE)
        );
    }

    @Test
    @DirtiesContext
    void whenGetCouriersWithDifferentParameters_thenReturnValidSlice() throws Exception {
        // given
        CreateCouriersResponse createCouriersResponse = courierUtil.createCouriers(
                new CreateCourierRequest(List.of(
                        new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00")),
                        new CreateCourierDto(CourierType.BIKE, List.of(2, 3, 5), List.of("08:00-08:50")),
                        new CreateCourierDto(CourierType.AUTO, List.of(5, 6), List.of("10:00-12:00", "13:00-15:00", "17:30-20:00"))
                ))
        );
        List<CourierDto> createdCourierDtos = createCouriersResponse.getCouriers();

        // when + then
        assertThat(courierUtil.getCouriers(0, 1).getCouriers(), is(equalTo(createdCourierDtos.subList(0, 1))));
        assertThat(courierUtil.getCouriers(0, 2).getCouriers(), is(equalTo(createdCourierDtos.subList(0, 2))));

        assertThat(courierUtil.getCouriers(1, 1).getCouriers(), is(equalTo(createdCourierDtos.subList(1, 2))));
        assertThat(courierUtil.getCouriers(1, 2).getCouriers(), is(equalTo(createdCourierDtos.subList(1, 3))));
        assertThat(courierUtil.getCouriers(1, 3).getCouriers(), is(equalTo(createdCourierDtos.subList(1, 3))));

        assertThat(courierUtil.getCouriers(2, 1).getCouriers(), is(equalTo(createdCourierDtos.subList(2, 3))));
        assertThat(courierUtil.getCouriers(2, 2).getCouriers(), is(equalTo(createdCourierDtos.subList(2, 3))));
        assertThat(courierUtil.getCouriers(2, 3).getCouriers(), is(equalTo(createdCourierDtos.subList(2, 3))));

        assertThat(courierUtil.getCouriers(3, 1).getCouriers(), is(emptyIterable()));
        assertThat(courierUtil.getCouriers(3, 2).getCouriers(), is(emptyIterable()));
        assertThat(courierUtil.getCouriers(3, 3).getCouriers(), is(emptyIterable()));
    }
}