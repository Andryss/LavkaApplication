package ru.andryss.lavka.controller;

import org.hamcrest.MatcherAssert;
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
import ru.andryss.lavka.objects.dto.CourierDto;
import ru.andryss.lavka.objects.dto.CourierType;
import ru.andryss.lavka.objects.mapping.assign.order.CouriersGroupOrders;
import ru.andryss.lavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrder;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCourierDto;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderDto;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderRequest;
import ru.andryss.lavka.objects.mapping.get.courier.GetCourierResponse;
import ru.andryss.lavka.objects.mapping.get.courier.GetCouriersResponse;
import ru.andryss.lavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    OrderUtil orderUtil;

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
        MatcherAssert.assertThat(courierUtil.getCouriers(0, 1).getCouriers(), is(equalTo(createdCourierDtos.subList(0, 1))));
        MatcherAssert.assertThat(courierUtil.getCouriers(0, 2).getCouriers(), is(equalTo(createdCourierDtos.subList(0, 2))));

        MatcherAssert.assertThat(courierUtil.getCouriers(1, 1).getCouriers(), is(equalTo(createdCourierDtos.subList(1, 2))));
        MatcherAssert.assertThat(courierUtil.getCouriers(1, 2).getCouriers(), is(equalTo(createdCourierDtos.subList(1, 3))));
        MatcherAssert.assertThat(courierUtil.getCouriers(1, 3).getCouriers(), is(equalTo(createdCourierDtos.subList(1, 3))));

        MatcherAssert.assertThat(courierUtil.getCouriers(2, 1).getCouriers(), is(equalTo(createdCourierDtos.subList(2, 3))));
        MatcherAssert.assertThat(courierUtil.getCouriers(2, 2).getCouriers(), is(equalTo(createdCourierDtos.subList(2, 3))));
        MatcherAssert.assertThat(courierUtil.getCouriers(2, 3).getCouriers(), is(equalTo(createdCourierDtos.subList(2, 3))));

        MatcherAssert.assertThat(courierUtil.getCouriers(3, 1).getCouriers(), is(emptyIterable()));
        MatcherAssert.assertThat(courierUtil.getCouriers(3, 2).getCouriers(), is(emptyIterable()));
        MatcherAssert.assertThat(courierUtil.getCouriers(3, 3).getCouriers(), is(emptyIterable()));
    }

    @Test
    @DirtiesContext
    void whenGetCourierAssignments_thenReturnEqualOnes() throws Exception {
        // given
        orderUtil.createOrders(new CreateOrderRequest(List.of(
                new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10)
        )));
        CreateCouriersResponse createCouriersResponse = courierUtil.createCouriers(new CreateCourierRequest(List.of(
                new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00"))
        )));
        CourierDto createdCourierDto = createCouriersResponse.getCouriers().get(0);
        OrderAssignResponse orderAssignments = orderUtil.assignOrders("2000-02-10");
        CouriersGroupOrders courierAssignment = orderAssignments.getCouriers().get(0);

        // when
        OrderAssignResponse response = courierUtil.getCourierAssignments("2000-02-10", createdCourierDto.getCourierId());

        // then
        assertThat(response.getDate(), is(equalTo("2000-02-10")));
        assertThat(response.getCouriers(), is(iterableWithSize(1)));
        assertThat(response.getCouriers().get(0), is(equalTo(courierAssignment)));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidGetCourierAssignmentsParameters")
    void whenGetCourierAssignmentsWithInvalidParameters_thenReturnBadRequest(String date, Long courierId) throws Exception {
        courierUtil.getCourierAssignmentsReturnResult(date, courierId)
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidGetCourierAssignmentsParameters() {
        return Stream.of(
                Arguments.of("invalid", 1L),
                Arguments.of("2000-02-30", 1L),
                Arguments.of("2000-02-50", 1L),
                Arguments.of("20000-02-10", 1L),
                Arguments.of("2000-13-10", 1L),
                Arguments.of("2000-02-20", null),
                Arguments.of("2000-02-20", 1L) // non-existing courier
        );
    }

    @ParameterizedTest
    @MethodSource("provideCourierTypesWithMetaInfoCoefficients")
    @DirtiesContext
    void whenGetCourierMetaInfo_thenReturnCorrectScore(CourierType courierType, Integer earningsCoefficient, Integer ratingsCoefficient) throws Exception {
        // given
        List<CreateOrderDto> createOrderDtoList = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) createOrderDtoList.add(new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10));
        CreateOrderResponse createOrderResponse = orderUtil.createOrders(new CreateOrderRequest(createOrderDtoList));
        CreateCouriersResponse createCouriersResponse = courierUtil.createCouriers(new CreateCourierRequest(List.of(
                new CreateCourierDto(courierType, List.of(1), List.of("10:00-12:00", "13:00-17:00"))
        )));
        orderUtil.assignOrders("2000-02-10");
        CourierDto createdCourierDto = createCouriersResponse.getCouriers().get(0);
        Long createdCourierId = createdCourierDto.getCourierId();

        LocalDateTime completeTime = LocalDate.parse("2000-02-10").atTime(11, 30);
        List<CompleteOrder> completeOrderList = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) completeOrderList.add(new CompleteOrder(createdCourierId, createOrderResponse.getOrders().get(i).getOrderId(), completeTime));

        orderUtil.completeOrders(new CompleteOrderRequestDto(completeOrderList));

        // when
        GetCourierMetaInfoResponse response = courierUtil.getCourierMetaInfo(createdCourierId, "2000-02-10", "2000-02-11");

        // then
        assertThat(response.getCourierId(), is(equalTo(createdCourierDto.getCourierId())));
        assertThat(response.getCourierType(), is(equalTo(courierType)));
        assertThat(response.getRegions(), contains(1));
        assertThat(response.getWorkingHours(), is(equalTo(List.of("10:00-12:00", "13:00-17:00"))));

        assertThat(response.getEarnings(), is(equalTo(earningsCoefficient * 10 * 24)));
        assertThat(response.getRating(), is(equalTo(ratingsCoefficient)));
    }

    private static Stream<Arguments> provideCourierTypesWithMetaInfoCoefficients() {
        return Stream.of(
                Arguments.of(CourierType.FOOT, 2, 3),
                Arguments.of(CourierType.BIKE, 3, 2),
                Arguments.of(CourierType.AUTO, 4, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidGetCourierMetaInfoParameters")
    void whenGetCourierMetaInfoWithInvalidParameters_thenReturnBadRequest(Long courierId, String startDate, String endDate) throws Exception {
        // when + then
        courierUtil.getCourierMetaInfoReturnResult(courierId, startDate, endDate)
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidGetCourierMetaInfoParameters() {
        return Stream.of(
                // invalid courierId
                Arguments.of(1L, "2000-02-20", "2000-02-21"), // non-existing courier
                // invalid startDate
                Arguments.of(1L, "invalid", "2000-02-21"),
                Arguments.of(1L, "2000-02-30", "2000-02-21"),
                Arguments.of(1L, "2000-01-50", "2000-02-21"),
                Arguments.of(1L, "20000-02-10", "2000-02-21"),
                Arguments.of(1L, "2000-13-10", "2000-02-21"),
                // invalid endDate
                Arguments.of(1L, "2000-02-20", "invalid"),
                Arguments.of(1L, "2000-02-20", "2000-02-30"),
                Arguments.of(1L, "2000-02-20", "2000-02-50"),
                Arguments.of(1L, "2000-02-20", "20000-02-10"),
                Arguments.of(1L, "2000-02-20", "2000-13-10"),
                Arguments.of(1L, "2000-02-20", "2000-02-20"),
                Arguments.of(1L, "2000-02-20", "2000-02-19")
        );
    }
}