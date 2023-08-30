package ru.andryss.lavka.objects.mapping.get.courier.metainfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.andryss.lavka.objects.dto.CourierType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCourierMetaInfoResponse {
    Long courierId;
    CourierType courierType;
    List<Integer> regions;
    List<String> workingHours;
    Integer rating;
    Integer earnings;
}
