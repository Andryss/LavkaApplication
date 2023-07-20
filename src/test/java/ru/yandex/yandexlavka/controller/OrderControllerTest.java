package ru.yandex.yandexlavka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;


    private ResultActions createOrdersReturnResult(CreateOrderRequest request) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/orders");
        if (request != null) requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request));
        return mockMvc.perform(requestBuilder)
                .andDo(print());
    }

    private CreateOrderResponse createOrders(CreateOrderRequest request) throws Exception {
        MvcResult mvcResult = createOrdersReturnResult(request)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, CreateOrderResponse.class);
    }

    @Test
    @DirtiesContext
    void whenCreateOrder_thenReturnEqualResponse() throws Exception {
        // given
        CreateOrderDto orderDto = new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10);
        CreateOrderRequest request = new CreateOrderRequest(List.of(orderDto));

        // when
        CreateOrderResponse response = createOrders(request);

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
        createOrdersReturnResult(request)
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


    private ResultActions getOrderByIdReturnResult(Long orderId) throws Exception {
        return mockMvc.perform(get("/orders/{orderId}", orderId))
                .andDo(print());
    }

    private GetOrderResponse getOrderById(Long orderId) throws Exception {
        MvcResult mvcResult = getOrderByIdReturnResult(orderId)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, GetOrderResponse.class);
    }

    @Test
    @DirtiesContext
    void whenGetOrderById_thenReturnEqualOne() throws Exception {
        // given
        CreateOrderResponse createOrderResponse = createOrders(
                new CreateOrderRequest(List.of(new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10)))
        );
        OrderDto orderDto = createOrderResponse.getOrders().get(0);
        Long orderId = orderDto.getOrderId();

        // when
        GetOrderResponse response = getOrderById(orderId);

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
        getOrderByIdReturnResult(Long.MIN_VALUE)
                .andExpect(status().isNotFound());
    }


    private ResultActions getOrdersReturnResult(Integer offset, Integer limit) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/orders");
        if (offset != null) requestBuilder
                .param("offset", String.valueOf(offset));
        if (limit != null) requestBuilder
                .param("limit", String.valueOf(limit));
        return mockMvc.perform(requestBuilder)
                .andDo(print());
    }

    private GetOrdersResponse getOrders(Integer offset, Integer limit) throws Exception {
        MvcResult mvcResult = getOrdersReturnResult(offset, limit)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, GetOrdersResponse.class);
    }

    @Test
    @DirtiesContext
    void whenGetOrders_thenReturnAllEqualOnes() throws Exception {
        // given
        CreateOrderResponse createOrderResponse = createOrders(
                new CreateOrderRequest(List.of(
                        new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10),
                        new CreateOrderDto(10.0f, 2, List.of("22:00-22:30"), 2),
                        new CreateOrderDto(7.5f, 1, List.of("08:00-11:00", "12:00-17:30", "17:50-18:00"), 50)
                ))
        );
        List<OrderDto> createdOrderDtos = createOrderResponse.getOrders();

        // when
        GetOrdersResponse response = getOrders(0, 3);

        // then
        List<OrderDto> foundOrderDtos = response.getOrders();
        assertThat(foundOrderDtos, is(iterableWithSize(3)));
        assertThat(foundOrderDtos, is(equalTo(createdOrderDtos)));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidGetOrdersParameters")
    void whenGetOrdersWithInvalidParameters_thenReturnBadRequest(Integer offset, Integer limit) throws Exception {
        // when + then
        getOrdersReturnResult(offset, limit)
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
        CreateOrderResponse createOrderResponse = createOrders(
                new CreateOrderRequest(List.of(
                        new CreateOrderDto(2.0f, 1, List.of("10:00-12:00", "13:00-17:00"), 10),
                        new CreateOrderDto(10.0f, 2, List.of("22:00-22:30"), 2),
                        new CreateOrderDto(7.5f, 1, List.of("08:00-11:00", "12:00-17:30", "17:50-18:00"), 50)
                ))
        );
        List<OrderDto> createdOrderDtos = createOrderResponse.getOrders();

        // when + then
        assertThat(getOrders(0, 1).getOrders(), is(equalTo(createdOrderDtos.subList(0, 1))));
        assertThat(getOrders(0, 2).getOrders(), is(equalTo(createdOrderDtos.subList(0, 2))));

        assertThat(getOrders(1, 1).getOrders(), is(equalTo(createdOrderDtos.subList(1, 2))));
        assertThat(getOrders(1, 2).getOrders(), is(equalTo(createdOrderDtos.subList(1, 3))));
        assertThat(getOrders(1, 3).getOrders(), is(equalTo(createdOrderDtos.subList(1, 3))));

        assertThat(getOrders(2, 1).getOrders(), is(equalTo(createdOrderDtos.subList(2, 3))));
        assertThat(getOrders(2, 2).getOrders(), is(equalTo(createdOrderDtos.subList(2, 3))));
        assertThat(getOrders(2, 3).getOrders(), is(equalTo(createdOrderDtos.subList(2, 3))));

        assertThat(getOrders(3, 1).getOrders(), is(emptyIterable()));
        assertThat(getOrders(3, 2).getOrders(), is(emptyIterable()));
        assertThat(getOrders(3, 3).getOrders(), is(emptyIterable()));
    }
}