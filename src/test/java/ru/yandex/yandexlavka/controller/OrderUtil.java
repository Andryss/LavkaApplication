package ru.yandex.yandexlavka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.get.order.GetOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.get.order.GetOrdersResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class OrderUtil {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    public ResultActions createOrdersReturnResult(CreateOrderRequest request) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/orders");
        if (request != null) requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request));
        return mockMvc.perform(requestBuilder)
                .andDo(print());
    }

    public CreateOrderResponse createOrders(CreateOrderRequest request) throws Exception {
        MvcResult mvcResult = createOrdersReturnResult(request)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, CreateOrderResponse.class);
    }

    public ResultActions getOrderByIdReturnResult(Long orderId) throws Exception {
        return mockMvc.perform(get("/orders/{orderId}", orderId))
                .andDo(print());
    }

    public GetOrderResponse getOrderById(Long orderId) throws Exception {
        MvcResult mvcResult = getOrderByIdReturnResult(orderId)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, GetOrderResponse.class);
    }

    public ResultActions getOrdersReturnResult(Integer offset, Integer limit) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/orders");
        if (offset != null) requestBuilder
                .param("offset", String.valueOf(offset));
        if (limit != null) requestBuilder
                .param("limit", String.valueOf(limit));
        return mockMvc.perform(requestBuilder)
                .andDo(print());
    }

    public GetOrdersResponse getOrders(Integer offset, Integer limit) throws Exception {
        MvcResult mvcResult = getOrdersReturnResult(offset, limit)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, GetOrdersResponse.class);
    }

    public ResultActions assignOrdersReturnResult(String date) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/orders/assign");
        if (date != null) requestBuilder
                .param("date", date);
        return mockMvc.perform(requestBuilder)
                .andDo(print());
    }

    public OrderAssignResponse assignOrders(String date) throws Exception {
        MvcResult mvcResult = assignOrdersReturnResult(date)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, OrderAssignResponse.class);
    }

    public ResultActions completeOrdersReturnResult(CompleteOrderRequestDto request) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/orders/complete");
        if (request != null) requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request));
        return mockMvc.perform(requestBuilder)
                .andDo(print());
    }

    public CompleteOrderResponse completeOrders(CompleteOrderRequestDto request) throws Exception {
        MvcResult mvcResult = completeOrdersReturnResult(request)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, CompleteOrderResponse.class);
    }
}
