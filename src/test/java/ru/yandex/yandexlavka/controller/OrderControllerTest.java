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
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.mapping.assign.order.CouriersGroupOrders;
import ru.yandex.yandexlavka.objects.mapping.assign.order.GroupOrders;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrder;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierDto;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderDto;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.get.order.GetOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.get.order.GetOrdersResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    OrderUtil orderUtil;

    @Autowired
    CourierUtil courierUtil;

    @Autowired
    MockMvc mockMvc;
    

    @Test
    @DirtiesContext
    void whenCreateOrder_thenReturnEqualResponse() throws Exception {
        // given
        CreateOrderDto orderDto = new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10);
        CreateOrderRequest request = new CreateOrderRequest(List.of(orderDto));

        // when
        CreateOrderResponse response = orderUtil.createOrders(request);

        // then
        List<OrderDto> createdOrderDtos = response.getOrders();
        assertThat(createdOrderDtos, is(iterableWithSize(1)));

        OrderDto createdOrderDto = createdOrderDtos.get(0);
        assertThat(createdOrderDto.getOrderId(), is(notNullValue()));
        assertThat(createdOrderDto.getWeight(), is(equalTo(2.0f)));
        assertThat(createdOrderDto.getRegion(), is(equalTo(1)));
        assertThat(createdOrderDto.getDeliveryHours(), is(equalTo(List.of("10:00-12:00", "13:00-17:00"))));
        assertThat(createdOrderDto.getCost(), is(equalTo(10)));
        assertThat(createdOrderDto.getCompletedTime(), is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCreateOrderRequests")
    void whenCreateOrderWithInvalidData_thenReturnBadRequest(CreateOrderRequest request) throws Exception {
        // when + then
        orderUtil.createOrdersReturnResult(request)
                .andExpect(status().isBadRequest());
    }

    private static CreateOrderRequest[] provideInvalidCreateOrderRequests() {
        return new CreateOrderRequest[]{
                // invalid CreateOrderRequest
                null,
                new CreateOrderRequest(null),
                new CreateOrderRequest(List.of()),
                // invalid CreateOrderDto
                new CreateOrderRequest(Collections.singletonList(null)),
                // invalid CreateOrderDto weight
                new CreateOrderRequest(List.of(new CreateOrderDto(null, 1, List.of("10:00-12:00", "13:00-17:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(-1f, 1, List.of("10:00-12:00", "13:00-17:00"), 10))),
                // invalid CreateOrderDto region
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, null, List.of("10:00-12:00", "13:00-17:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 0, List.of("10:00-12:00", "13:00-17:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, -1, List.of("10:00-12:00", "13:00-17:00"), 10))),
                // invalid CreateOrderDto deliveryHours
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, null, 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of(), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, Collections.singletonList(null), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of(""), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("invalid"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("010:00-12:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("50:00-51:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10:60-12:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10::00-12:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("1000-12:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10:00--12:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10:00-09:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("00:00-00:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("23:59-00:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10:00-14:00", "13:00-17:00"), 10))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10:00-11:00", "11:00-12:00"), 10))),
                // invalid CreateOrderDto cost
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), null))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 0))),
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), -1))),
        };
    }
    

    @Test
    @DirtiesContext
    void whenGetOrderById_thenReturnEqualOne() throws Exception {
        // given
        CreateOrderResponse createOrderResponse = orderUtil.createOrders(
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10)))
        );
        OrderDto orderDto = createOrderResponse.getOrders().get(0);
        Long orderId = orderDto.getOrderId();

        // when
        GetOrderResponse response = orderUtil.getOrderById(orderId);

        // then
        OrderDto foundOrderDto = response.getOrder();
        assertThat(foundOrderDto, is(equalTo(orderDto)));
    }

    @Test
    void whenGetOrderWithInvalidId_thenReturnBadRequest() throws Exception {
        // when + then
        mockMvc.perform(get("/orders/invalidId"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetOrderByNonExistingId_thenReturnNotFound() throws Exception {
        // when + then
        orderUtil.getOrderByIdReturnResult(Long.MIN_VALUE)
                .andExpect(status().isNotFound());
    }
    

    @Test
    @DirtiesContext
    void whenGetOrders_thenReturnAllEqualOnes() throws Exception {
        // given
        CreateOrderResponse createOrderResponse = orderUtil.createOrders(
                new CreateOrderRequest(List.of(
                        new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10),
                        new CreateOrderDto(10.0f, 2, List.of("22:00-22:30"), 2),
                        new CreateOrderDto(7.5f, 1, List.of("08:00-11:00", "12:00-17:30", "17:50-18:00"), 50)
                ))
        );
        List<OrderDto> createdOrderDtos = createOrderResponse.getOrders();

        // when
        GetOrdersResponse response = orderUtil.getOrders(0, 3);

        // then
        List<OrderDto> foundOrderDtos = response.getOrders();
        assertThat(foundOrderDtos, is(iterableWithSize(3)));
        assertThat(foundOrderDtos, is(equalTo(createdOrderDtos)));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidGetOrdersParameters")
    void whenGetOrdersWithInvalidParameters_thenReturnBadRequest(Integer offset, Integer limit) throws Exception {
        // when + then
        orderUtil.getOrdersReturnResult(offset, limit)
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidGetOrdersParameters() {
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
    void whenGetOrdersWithDifferentParameters_thenReturnValidSlice() throws Exception {
        // given
        CreateOrderResponse createOrderResponse = orderUtil.createOrders(
                new CreateOrderRequest(List.of(
                        new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10),
                        new CreateOrderDto(10.0f, 2, List.of("22:00-22:30"), 2),
                        new CreateOrderDto(7.5f, 1, List.of("08:00-11:00", "12:00-17:30", "17:50-18:00"), 50)
                ))
        );
        List<OrderDto> createdOrderDtos = createOrderResponse.getOrders();

        // when + then
        assertThat(orderUtil.getOrders(0, 1).getOrders(), is(equalTo(createdOrderDtos.subList(0, 1))));
        assertThat(orderUtil.getOrders(0, 2).getOrders(), is(equalTo(createdOrderDtos.subList(0, 2))));

        assertThat(orderUtil.getOrders(1, 1).getOrders(), is(equalTo(createdOrderDtos.subList(1, 2))));
        assertThat(orderUtil.getOrders(1, 2).getOrders(), is(equalTo(createdOrderDtos.subList(1, 3))));
        assertThat(orderUtil.getOrders(1, 3).getOrders(), is(equalTo(createdOrderDtos.subList(1, 3))));

        assertThat(orderUtil.getOrders(2, 1).getOrders(), is(equalTo(createdOrderDtos.subList(2, 3))));
        assertThat(orderUtil.getOrders(2, 2).getOrders(), is(equalTo(createdOrderDtos.subList(2, 3))));
        assertThat(orderUtil.getOrders(2, 3).getOrders(), is(equalTo(createdOrderDtos.subList(2, 3))));

        assertThat(orderUtil.getOrders(3, 1).getOrders(), is(emptyIterable()));
        assertThat(orderUtil.getOrders(3, 2).getOrders(), is(emptyIterable()));
        assertThat(orderUtil.getOrders(3, 3).getOrders(), is(emptyIterable()));
    }

    @Test
    @DirtiesContext
    void whenAssignOrders_thenAssignAndReturnEqualOneResponse() throws Exception {
        // given
        CreateOrderResponse createOrderResponse = orderUtil.createOrders(new CreateOrderRequest(List.of(
                new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10)
        )));
        OrderDto createdOrderDto = createOrderResponse.getOrders().get(0);
        CreateCouriersResponse createCouriersResponse = courierUtil.createCouriers(new CreateCourierRequest(List.of(
                new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00"))
        )));
        CourierDto createdCourierDto = createCouriersResponse.getCouriers().get(0);

        // when
        OrderAssignResponse response = orderUtil.assignOrders("2000-02-10");

        // then
        assertThat(response.getDate(), is(equalTo("2000-02-10")));

        assertThat(response.getCouriers(), is(iterableWithSize(1)));
        CouriersGroupOrders courierGroupOrder = response.getCouriers().get(0);
        assertThat(courierGroupOrder.getCourierId(), is(equalTo(createdCourierDto.getCourierId())));

        assertThat(courierGroupOrder.getOrders(), is(iterableWithSize(1)));
        GroupOrders groupOrder = courierGroupOrder.getOrders().get(0);
        assertThat(groupOrder.getGroupOrderId(), is(notNullValue()));

        assertThat(groupOrder.getOrders(), is(iterableWithSize(1)));
        OrderDto order = groupOrder.getOrders().get(0);
        assertThat(order, is(equalTo(createdOrderDto)));
    }

    @Test
    @DirtiesContext
    void whenAssignOrdersAgain_thenReturnBadRequest() throws Exception {
        // given
        orderUtil.createOrders(new CreateOrderRequest(List.of(
                new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10)
        )));
        courierUtil.createCouriers(new CreateCourierRequest(List.of(
                new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00"))
        )));
        orderUtil.assignOrders("2000-02-10");

        // when + then
        orderUtil.assignOrdersReturnResult("2000-02-10")
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAssignOrdersParameters")
    void whenAssignOrdersWithInvalidParameters_thenReturnBadRequest(String date) throws Exception {
        // when + then
        orderUtil.assignOrdersReturnResult(date)
                .andExpect(status().isBadRequest());
    }

    private static String[] provideInvalidAssignOrdersParameters() {
        return new String[]{
                "invalid",
                "2000-02-30",
                "2000-02-50",
                "20000-02-10",
                "2000-13-10"
        };
    }

    @Test
    @DirtiesContext
    void whenAssignOrdersAdvanced_thenAssignAllOrders() throws Exception {
        // given
        CreateOrderResponse createOrderResponse = orderUtil.createOrders(new CreateOrderRequest(List.of(
                new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10),
                new CreateOrderDto(10.0f, 6, List.of("22:00-22:30"), 2),
                new CreateOrderDto(7.5f, 3, List.of("08:00-11:00", "12:00-17:30", "17:50-18:00"), 50)
        )));
        List<OrderDto> createdOrders = createOrderResponse.getOrders();
        courierUtil.createCouriers(new CreateCourierRequest(List.of(
                new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00")),
                new CreateCourierDto(CourierType.BIKE, List.of(1, 3, 5), List.of("08:00-08:50")),
                new CreateCourierDto(CourierType.AUTO, List.of(1, 6), List.of("10:00-12:00", "13:00-15:00", "17:30-23:00"))
        )));

        // when
        OrderAssignResponse response = orderUtil.assignOrders("2000-02-10");

        // then
        List<OrderDto> assignedOrderDtos = response.getCouriers().stream()
                .flatMap(cgo -> cgo.getOrders().stream())
                .flatMap(go -> go.getOrders().stream())
                .sorted(Comparator.comparingLong(OrderDto::getOrderId))
                .toList();
        assertThat(assignedOrderDtos, is(iterableWithSize(3)));
        assertThat(assignedOrderDtos, is(equalTo(createdOrders)));
    }

    @Test
    @DirtiesContext
    void whenCompleteOrder_thenSetCompletedTime() throws Exception {
        // given
        CreateOrderResponse createOrderResponse = orderUtil.createOrders(new CreateOrderRequest(List.of(
                new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10)
        )));
        OrderDto createdOrderDto = createOrderResponse.getOrders().get(0);
        CreateCouriersResponse createCouriersResponse = courierUtil.createCouriers(new CreateCourierRequest(List.of(
                new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00"))
        )));
        CourierDto createdCourierDto = createCouriersResponse.getCouriers().get(0);
        orderUtil.assignOrders("2000-02-10");

        Long createdOrderId = createdOrderDto.getOrderId();
        Long createdCourierId = createdCourierDto.getCourierId();
        LocalDateTime completeTime = LocalDate.parse("2000-02-10").atTime(11, 30);
        CompleteOrderRequestDto request = new CompleteOrderRequestDto(List.of(
                new CompleteOrder(createdCourierId, createdOrderId, completeTime)
        ));

        // when
        CompleteOrderResponse response = orderUtil.completeOrders(request);

        // then
        assertThat(response.getOrders(), is(iterableWithSize(1)));
        OrderDto completedOrder = response.getOrders().get(0);
        assertThat(completedOrder, is(equalTo(orderUtil.getOrderById(createdOrderId).getOrder())));
        assertThat(completedOrder.getCompletedTime(), is(equalTo(completeTime.toString())));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCompleteOrderRequests")
    void whenCompleteOrderWithInvalidData_thenReturnBadRequest(CompleteOrderRequestDto request) throws Exception {
        // when + then
        orderUtil.completeOrdersReturnResult(request)
                .andExpect(status().isBadRequest());
    }

    private static CompleteOrderRequestDto[] provideInvalidCompleteOrderRequests() {
        return new CompleteOrderRequestDto[]{
                null,
                new CompleteOrderRequestDto(null),
                new CompleteOrderRequestDto(List.of()),
                new CompleteOrderRequestDto(Collections.singletonList(null)),
                new CompleteOrderRequestDto(List.of(new CompleteOrder(null, 1L, LocalDateTime.MAX))),
                new CompleteOrderRequestDto(List.of(new CompleteOrder(1L, null, LocalDateTime.MAX))),
                new CompleteOrderRequestDto(List.of(new CompleteOrder(1L, 1L, null))),
                new CompleteOrderRequestDto(List.of(new CompleteOrder(1L, 1L, LocalDateTime.MAX))) // no such courier and order
        };
    }

    @Test
    @DirtiesContext
    void whenCompleteOrderWithInvalidButExistingData_thenReturnBadRequest() throws Exception {
        // given
        CreateOrderResponse createOrderResponse = orderUtil.createOrders(new CreateOrderRequest(List.of(
                new CreateOrderDto(2.0f, 1, List.of("10:00-12:00"), 10), // should be assigned to first
                new CreateOrderDto(2.0f, 5, List.of("10:00-12:00"), 10), // should be assigned to second
                new CreateOrderDto(1.0f, 2, List.of("00:00-00:10"), 50)  // not assigned
        )));
        CreateCouriersResponse createCouriersResponse = courierUtil.createCouriers(new CreateCourierRequest(List.of(
                new CreateCourierDto(CourierType.FOOT, List.of(1, 2, 3), List.of("10:00-12:00", "13:00-17:00")),
                new CreateCourierDto(CourierType.BIKE, List.of(2, 5, 6), List.of("10:00-12:00", "13:00-17:00"))
        )));
        orderUtil.assignOrders("2000-02-10");

        Long firstCourierId = createCouriersResponse.getCouriers().get(0).getCourierId();
        Long secondCourierId = createCouriersResponse.getCouriers().get(1).getCourierId();
        Long nonExistingUserId = 1 + Math.max(firstCourierId, secondCourierId);
        Long orderIdAssignedToFirst = createOrderResponse.getOrders().get(0).getOrderId();
        Long orderIdAssignedToSecond = createOrderResponse.getOrders().get(1).getOrderId();
        Long orderIdNotAssigned = createOrderResponse.getOrders().get(2).getOrderId();

        LocalDateTime correctCompleteTime = LocalDate.parse("2000-02-10").atTime(11, 30);
        LocalDateTime incorrectCompleteTime = LocalDate.parse("2000-02-10").atTime(13, 30);

        // when + then
        for (Arguments arguments : List.of(
                // incorrect courier id
                Arguments.of(secondCourierId, orderIdAssignedToFirst, correctCompleteTime),
                Arguments.of(nonExistingUserId, orderIdAssignedToFirst, correctCompleteTime),
                // incorrect order id
                Arguments.of(firstCourierId, orderIdAssignedToSecond, correctCompleteTime),
                Arguments.of(firstCourierId, orderIdNotAssigned, correctCompleteTime),
                // incorrect complete time
                Arguments.of(firstCourierId, orderIdAssignedToFirst, incorrectCompleteTime)
        )) {
            Object[] objects = arguments.get();
            orderUtil.completeOrdersReturnResult(new CompleteOrderRequestDto(List.of(
                    new CompleteOrder((Long) objects[0], (Long) objects[1], (LocalDateTime) objects[2])
            ))).andExpect(status().isBadRequest());
        }
    }
}