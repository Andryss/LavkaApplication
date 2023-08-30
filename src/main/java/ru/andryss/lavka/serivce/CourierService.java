package ru.andryss.lavka.serivce;

import ru.andryss.lavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.andryss.lavka.objects.mapping.get.courier.GetCourierResponse;
import ru.andryss.lavka.objects.mapping.get.courier.GetCouriersResponse;
import ru.andryss.lavka.objects.mapping.get.courier.metainfo.GetCourierMetaInfoResponse;

import java.time.LocalDate;

public interface CourierService {

    CreateCouriersResponse addCouriers(CreateCourierRequest request);

    GetCourierResponse getCourierById(Long courierId);

    GetCouriersResponse getCourierRange(Integer offset, Integer limit);

    GetCourierMetaInfoResponse getCourierMetaInfo(Long courierId, LocalDate startDate, LocalDate endDate);

    OrderAssignResponse getCouriersAssignments(LocalDate date, Long courierId);

}
