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
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderDto;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.get.order.GetOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.get.order.GetOrdersResponse;

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
class OrderControllerTest {

    @Autowired
    OrderUtil orderUtil;

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
}