package ru.yandex.yandexlavka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.mapping.assign.order.CouriersGroupOrders;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.util.HTTPSender;
import ru.yandex.yandexlavka.util.generator.CourierGenerator;
import ru.yandex.yandexlavka.util.generator.OrderGenerator;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class YandexLavkaApplicationTests {

    @Value("http://localhost:${local.server.port}")
    private String defaultURL;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final HTTPSender httpSender;
    private final CourierGenerator courierGenerator;
    private final OrderGenerator orderGenerator;
    private final ObjectMapper mapper;

    @Autowired
    YandexLavkaApplicationTests(HTTPSender httpSender, CourierGenerator courierGenerator, OrderGenerator orderGenerator, ObjectMapper mapper) {
        this.httpSender = httpSender;
        this.courierGenerator = courierGenerator;
        this.orderGenerator = orderGenerator;
        this.mapper = mapper;
    }

    private final Set<Long> addedCouriersIds = new HashSet<>();
    private final Set<Long> addedOrdersIds = new HashSet<>();

    private final LocalDate assignDate = LocalDate.now();
    private final Map<Long,List<Long>> assignments = new HashMap<>();


    @SneakyThrows
    @BeforeAll
    void addSomeCouriers() {
        System.out.println("Adding some couriers");

        String url = defaultURL + "/couriers";

        for (int i = 0; i < 100; i++){
            long start = System.nanoTime();

            CreateCourierRequest createCourierRequest = courierGenerator.createCreateCourierRequest();
            String request = mapper.writeValueAsString(createCourierRequest);
            HttpResponse<String> httpResponse = httpSender.sendPostRequest(httpClient, url, request);

            long finish = System.nanoTime();
            System.out.println(((finish - start) / 1_000_000) + "ms >>> " + httpResponse);

            if (httpResponse.statusCode() != 200) continue;

            CreateCouriersResponse response = mapper.readValue(httpResponse.body(), CreateCouriersResponse.class);
            response.getCouriers().forEach(courierDto -> addedCouriersIds.add(courierDto.getCourierId()));
        }

        System.out.println("Added " + addedCouriersIds.size() + " couriers");
    }

    @SneakyThrows
    @BeforeAll
    void addSomeOrders() {
        System.out.println("Adding some orders");

        String url = defaultURL + "/orders";

        for (int i = 0; i < 100; i++){
            long start = System.nanoTime();

            CreateOrderRequest createOrderRequest = orderGenerator.createCreateOrderRequest();
            String request = mapper.writeValueAsString(createOrderRequest);
            HttpResponse<String> httpResponse = httpSender.sendPostRequest(httpClient, url, request);

            long finish = System.nanoTime();
            System.out.println(((finish - start) / 1_000_000) + "ms >>> " + httpResponse);

            if (httpResponse.statusCode() != 200) continue;

            List<OrderDto> response = mapper.readValue(httpResponse.body(), new TypeReference<>() {});
            response.forEach(orderDto -> addedOrdersIds.add(orderDto.getOrderId()));
        }

        System.out.println("Added " + addedOrdersIds.size() + " orders");
    }

    @SneakyThrows
    @Test
    @Order(1)
    void assignOrdersToCouriers() {
        System.out.println("Assign orders");

        String url = defaultURL + "/orders/assign?date=" + assignDate;

        long start = System.nanoTime();

        HttpResponse<String> httpResponse = httpSender.sendPostRequest(httpClient, url, "");

        long finish = System.nanoTime();
        System.out.println(((finish - start) / 1_000_000) + "ms >>> " + httpResponse);

        assertThat(httpResponse.statusCode(), is(201));

        List<OrderAssignResponse> response = mapper.readValue(httpResponse.body(), new TypeReference<>() {});
        OrderAssignResponse assignResponse = response.get(0);

        int[] assignedOrders = {0};
        assignResponse.getCouriers().forEach(couriersGroupOrders -> {
            Long courierId = couriersGroupOrders.getCourierId();
            couriersGroupOrders.getOrders().forEach(groupOrders ->{
                groupOrders.getOrders().forEach(orderDto ->
                        assignments.computeIfAbsent(courierId, l -> new ArrayList<>()).add(orderDto.getOrderId())
                );
                assignedOrders[0] += groupOrders.getOrders().size();
            });
        });

        System.out.println("Assigned " + assignedOrders[0] + " orders");
    }

    @Test
    @Order(2)
    void getOneCourierRequest() {
        System.out.println("Perform GET /courier/{id} request");

        String url = defaultURL + "/couriers/";

        for (Long addedCourierId : addedCouriersIds) {
            long start = System.nanoTime();

            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + addedCourierId);

            long finish = System.nanoTime();
            System.out.println(((finish - start) / 1_000_000) + "ms >>> " + httpResponse);

            assertThat(httpResponse.statusCode(), is(200));
        }

        long maxAddedId = addedCouriersIds.stream().mapToLong(Long::longValue).max().orElse(0);
        for (long i = maxAddedId + 1; i < maxAddedId + 101; i++){
            long start = System.nanoTime();

            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + i);

            long finish = System.nanoTime();
            System.out.println(((finish - start) / 1_000_000) + "ms >>> " + httpResponse);

            assertThat(httpResponse.statusCode(), is(404));
        }

        System.out.println("Performed");
    }

    @Test
    @Order(3)
    void getAllCouriersRequest() {
        System.out.println("Perform GET /couriers request");

        String url = defaultURL + "/couriers?limit=1000000";

        for (int i = 0; i < 100; i++){
            long start = System.nanoTime();

            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url);

            long finish = System.nanoTime();
            System.out.println(((finish - start) / 1_000_000) + "ms >>> " + httpResponse);

            assertThat(httpResponse.statusCode(), is(200));
        }

        System.out.println("Performed");
    }

    @Test
    @Order(4)
    void getOneOrderRequest() {
        System.out.println("Perform GET /order/{id} request");

        String url = defaultURL + "/orders/";

        for (Long addedOrderId : addedOrdersIds) {
            long start = System.nanoTime();

            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + addedOrderId);

            long finish = System.nanoTime();
            System.out.println(((finish - start) / 1_000_000) + "ms >>> " + httpResponse);

            assertThat(httpResponse.statusCode(), is(200));
        }

        long maxAddedId = addedOrdersIds.stream().mapToLong(Long::longValue).max().orElse(0);
        for (long i = maxAddedId + 1; i < maxAddedId + 101; i++){
            long start = System.nanoTime();

            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + i);

            long finish = System.nanoTime();
            System.out.println(((finish - start) / 1_000_000) + "ms >>> " + httpResponse);

            assertThat(httpResponse.statusCode(), is(404));
        }

        System.out.println("Performed");
    }

    @Test
    @Order(5)
    void getAllOrdersRequest() {
        System.out.println("Perform GET /orders request");

        String url = defaultURL + "/orders?limit=1000000";

        for (int i = 0; i < 100; i++){
            long start = System.nanoTime();

            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url);

            long finish = System.nanoTime();
            System.out.println(((finish - start) / 1_000_000) + "ms >>> " + httpResponse);

            assertThat(httpResponse.statusCode(), is(200));
        }

        System.out.println("Performed");
    }

    @SneakyThrows
    @Test
    @Order(6)
    void getCourierAssignmentsRequest() {
        System.out.println("Perform GET /couriers/assignments request");

        String url = defaultURL + "/couriers/assignments?date=" + assignDate + "&courier_id=";

        Set<Long> courierIds = assignments.keySet();

        for (Long courierId : courierIds) {
            long start = System.nanoTime();

            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + courierId);

            long finish = System.nanoTime();
            System.out.println(((finish - start) / 1_000_000) + "ms >>> " + httpResponse);

            assertThat(httpResponse.statusCode(), is(200));

            OrderAssignResponse response = mapper.readValue(httpResponse.body(), OrderAssignResponse.class);
            CouriersGroupOrders couriersGroupOrders = response.getCouriers().get(0);

            assertThat(couriersGroupOrders.getCourierId(), is(equalTo(courierId)));

            List<Long> assignedOrderList = couriersGroupOrders.getOrders().stream()
                    .flatMap(groupOrders -> groupOrders.getOrders().stream())
                    .map(OrderDto::getOrderId)
                    .sorted()
                    .toList();

            List<Long> assignedOrderListTheoretical = assignments.get(courierId);
            Collections.sort(assignedOrderListTheoretical);

            assertThat(assignedOrderListTheoretical, is(equalTo(assignedOrderList)));
        }

        System.out.println("Performed");
    }

}
