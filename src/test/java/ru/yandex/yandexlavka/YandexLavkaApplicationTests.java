package ru.yandex.yandexlavka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;
import ru.yandex.yandexlavka.objects.mapping.assign.order.CouriersGroupOrders;
import ru.yandex.yandexlavka.objects.mapping.assign.order.GroupOrders;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrder;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCouriersResponse;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.objects.utils.IntervalEntityUtils;
import ru.yandex.yandexlavka.repository.CourierRepository;
import ru.yandex.yandexlavka.repository.OrderRepository;
import ru.yandex.yandexlavka.util.HTTPSender;
import ru.yandex.yandexlavka.util.generator.CourierGenerator;
import ru.yandex.yandexlavka.util.generator.OrderGenerator;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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


    @BeforeAll
    void setUp() {
        System.out.println("Perform createCouriers request");
        int addedCouriers = addCouriers0();
        System.out.println("Created " + addedCouriers + " couriers");

        System.out.println("Perform createOrders request");
        int addedOrders = addOrders0();
        System.out.println("Created " + addedOrders + " orders");

        System.out.println("Perform assignOrders request");
        int assignedOrders = assignOrders0();
        System.out.println("Assigned " + assignedOrders + " orders");

//        System.out.println("Perform completeOrders request");
//        int completedOrders = completeOrders0();
//        System.out.println("Completed " + completedOrders + " orders");
    }

    @SneakyThrows
    int addCouriers0() {
        String url = defaultURL + "/couriers";

        int addedCouriers = 0;
        for (int i = 0; i < 100; i++){
            CreateCourierRequest createCourierRequest = courierGenerator.createCreateCourierRequest();
            HttpResponse<String> httpResponse = httpSender.sendPostRequest(httpClient, url, createCourierRequest);
            if (httpResponse.statusCode() != 200) continue;
            CreateCouriersResponse response = mapper.readValue(httpResponse.body(), CreateCouriersResponse.class);
            response.getCouriers().forEach(courierDto -> addedCouriersIds.add(courierDto.getCourierId()));
            addedCouriers += response.getCouriers().size();
        }
        return addedCouriers;
    }

    @SneakyThrows
    int addOrders0() {
        String url = defaultURL + "/orders";

        int addedOrders = 0;
        for (int i = 0; i < 100; i++){
            CreateOrderRequest createOrderRequest = orderGenerator.createCreateOrderRequest();
            HttpResponse<String> httpResponse = httpSender.sendPostRequest(httpClient, url, createOrderRequest);
            if (httpResponse.statusCode() != 200) continue;
            List<OrderDto> response = mapper.readValue(httpResponse.body(), new TypeReference<>() {});
            response.forEach(orderDto -> addedOrdersIds.add(orderDto.getOrderId()));
            addedOrders += response.size();
        }
        return addedOrders;
    }

    @SneakyThrows
    int assignOrders0() {
        String url = defaultURL + "/orders/assign?date=" + assignDate;

        HttpResponse<String> httpResponse = httpSender.sendPostRequest(httpClient, url);

        assertThat(httpResponse.statusCode(), is(201));

        List<OrderAssignResponse> response = mapper.readValue(httpResponse.body(), new TypeReference<>() {});
        OrderAssignResponse assignResponse = response.get(0);

        int assignedOrders = 0;
        for (CouriersGroupOrders couriersGroupOrders : assignResponse.getCouriers()) {
            Long courierId = couriersGroupOrders.getCourierId();
            for (GroupOrders groupOrders : couriersGroupOrders.getOrders()) {
                List<Long> idList = groupOrders.getOrders().stream().map(OrderDto::getOrderId).toList();
                assignments.computeIfAbsent(courierId, l -> new ArrayList<>()).addAll(idList);
                assignedOrders += groupOrders.getOrders().size();
            }
        }
        return assignedOrders;
    }

    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private OrderRepository orderRepository;

    int completeOrders0() {
        String url = defaultURL + "/orders/complete";
        List<CompleteOrder> completeOrderList = new ArrayList<>();

        AtomicInteger completedOrder = new AtomicInteger(0);
        assignments.forEach(((courierId, orderIds) -> {
            CourierEntity courierEntity = courierRepository.findById(courierId).orElseThrow();
            orderIds.forEach(orderId -> {
                OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow();
                LocalTime intersectionPointBetween = IntervalEntityUtils.getIntersectionPointBetween(courierEntity.getWorkingHours(), orderEntity.getDeliveryHours());
                completeOrderList.add(new CompleteOrder(
                        courierId,
                        orderId,
                        intersectionPointBetween.atDate(assignDate)
                ));
                completedOrder.incrementAndGet();
            });
        }));

        CompleteOrderRequestDto completeOrderRequestDto = new CompleteOrderRequestDto(completeOrderList);
        HttpResponse<String> httpResponse = httpSender.sendPostRequest(httpClient, url, completeOrderRequestDto);
        assertThat(httpResponse.statusCode(), is(200));

        return completedOrder.get();
    }

    @Test
    void getOneCourier() {
        System.out.println("Perform GET /courier/{id} request");

        String url = defaultURL + "/couriers/";

        for (Long addedCourierId : addedCouriersIds) {
            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + addedCourierId);
            assertThat(httpResponse.statusCode(), is(200));
        }

        long maxAddedId = addedCouriersIds.stream().mapToLong(Long::longValue).max().orElse(0);
        for (long i = maxAddedId + 1; i < maxAddedId + 101; i++){
            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + i);
            assertThat(httpResponse.statusCode(), is(404));
        }

        System.out.println("Performed");
    }

    @Test
    void getAllCouriers() {
        System.out.println("Perform GET /couriers request");

        String url = defaultURL + "/couriers?limit=1000000";

        for (int i = 0; i < 100; i++){
            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url);
            assertThat(httpResponse.statusCode(), is(200));
        }

        System.out.println("Performed");
    }

    @Test
    void getOneOrder() {
        System.out.println("Perform GET /order/{id} request");

        String url = defaultURL + "/orders/";

        for (Long addedOrderId : addedOrdersIds) {
            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + addedOrderId);
            assertThat(httpResponse.statusCode(), is(200));
        }

        long maxAddedId = addedOrdersIds.stream().mapToLong(Long::longValue).max().orElse(0);
        for (long i = maxAddedId + 1; i < maxAddedId + 101; i++){
            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + i);
            assertThat(httpResponse.statusCode(), is(404));
        }

        System.out.println("Performed");
    }

    @Test
    void getAllOrders() {
        System.out.println("Perform GET /orders request");

        String url = defaultURL + "/orders?limit=1000000";

        for (int i = 0; i < 100; i++){
            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url);
            assertThat(httpResponse.statusCode(), is(200));
        }

        System.out.println("Performed");
    }

    @SneakyThrows
    @Test
    void getCourierAssignments() {
        System.out.println("Perform GET /couriers/assignments request");

        String url = defaultURL + "/couriers/assignments?date=" + assignDate + "&courier_id=";

        for (Long courierId : assignments.keySet()) {
            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + courierId);
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

    @Test
    @Disabled("No completed orders yet")
    void getCourierMetaInfo() {
        System.out.println("Perform GET /couriers/meta-info/{id} request");

        String url = defaultURL + "/couriers/meta-info/";
        String urlTail = "?startDate=" + assignDate + "&endDate=" + assignDate.plusDays(1);

        assignments.keySet().forEach(courierId -> {
            HttpResponse<String> httpResponse = httpSender.sendGetRequest(httpClient, url + courierId + urlTail);
            assertThat(httpResponse.statusCode(), is(200));
        });

        System.out.println("Performed");
    }
}
