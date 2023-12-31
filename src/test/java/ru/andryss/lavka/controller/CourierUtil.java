package ru.andryss.lavka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.andryss.lavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.andryss.lavka.objects.mapping.get.courier.GetCourierResponse;
import ru.andryss.lavka.objects.mapping.get.courier.GetCouriersResponse;
import ru.andryss.lavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class CourierUtil {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    public ResultActions createCouriersReturnResult(CreateCourierRequest request) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/couriers");
        if (request != null) requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request));
        return mockMvc.perform(requestBuilder)
                .andDo(print());
    }

    public CreateCouriersResponse createCouriers(CreateCourierRequest request) throws Exception {
        MvcResult mvcResult = createCouriersReturnResult(request)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, CreateCouriersResponse.class);
    }

    public ResultActions getCourierByIdReturnResult(Long courierId) throws Exception {
        return mockMvc.perform(get("/couriers/{courierId}", courierId))
                .andDo(print());
    }

    public GetCourierResponse getCourierById(Long courierId) throws Exception {
        MvcResult mvcResult = getCourierByIdReturnResult(courierId)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, GetCourierResponse.class);
    }

    public ResultActions getCouriersReturnResult(Integer offset, Integer limit) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/couriers");
        if (offset != null) requestBuilder
                .param("offset", String.valueOf(offset));
        if (limit != null) requestBuilder
                .param("limit", String.valueOf(limit));
        return mockMvc.perform(requestBuilder)
                .andDo(print());
    }

    public GetCouriersResponse getCouriers(Integer offset, Integer limit) throws Exception {
        MvcResult mvcResult = getCouriersReturnResult(offset, limit)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, GetCouriersResponse.class);
    }

    public ResultActions getCourierAssignmentsReturnResult(String date, Long courierId) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/couriers/assignments");
        if (date != null) requestBuilder
                .param("date", date);
        if (courierId != null) requestBuilder
                .param("courier_id", String.valueOf(courierId));
        return mockMvc.perform(requestBuilder)
                .andDo(print());
    }

    public OrderAssignResponse getCourierAssignments(String date, Long courierId) throws Exception {
        MvcResult mvcResult = getCourierAssignmentsReturnResult(date, courierId)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, OrderAssignResponse.class);
    }

    public ResultActions getCourierMetaInfoReturnResult(Long courierId, String startDate, String endDate) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/couriers/meta-info/{coueirId}", courierId);
        if (startDate != null) requestBuilder
                .param("start_date", startDate);
        if (endDate != null) requestBuilder
                .param("end_date", endDate);
        return mockMvc.perform(requestBuilder)
                .andDo(print());
    }

    public GetCourierMetaInfoResponse getCourierMetaInfo(Long courierId, String startDate, String endDate) throws Exception {
        MvcResult mvcResult = getCourierMetaInfoReturnResult(courierId, startDate, endDate)
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(content, GetCourierMetaInfoResponse.class);
    }
}
