package ru.yandex.yandexlavka.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.function.Supplier;

@Component
public class HTTPSender {

    private final ObjectMapper objectMapper;

    @Autowired
    public HTTPSender(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public HttpResponse<String> sendPostRequest(HttpClient client, String url) {
        return logTime(() -> sendPostRequest0(client, url));
    }

    @SneakyThrows
    public HttpResponse<String> sendPostRequest(HttpClient client, String url, Object body) {
        return logTime(() -> {
            try {
                String bodyString = objectMapper.writeValueAsString(body);
                return sendPostRequest0(client, url, bodyString);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SneakyThrows
    public HttpResponse<String> sendGetRequest(HttpClient client, String url) {
        return logTime(() -> sendGetRequest0(client, url));
    }

    private <T> T logTime(Supplier<T> action) {
        long startTime = System.nanoTime();
        T result = action.get();
        long endTime = System.nanoTime();
        System.out.println(((endTime - startTime) / 1_000_000) + "ms >>> " + result);
        return result;
    }

    private HttpResponse<String> sendPostRequest0(HttpClient client, String url) {
        return sendPostRequest0(client, url, "");
    }

    @SneakyThrows
    private HttpResponse<String> sendPostRequest0(HttpClient client, String url, String body) {
        return client.send(
                HttpRequest.newBuilder(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(BodyPublishers.ofString(body))
                        .build(),
                BodyHandlers.ofString()
        );
    }

    @SneakyThrows
    private HttpResponse<String> sendGetRequest0(HttpClient client, String url) {
        return client.send(
                HttpRequest.newBuilder(URI.create(url))
                        .GET()
                        .build(),
                BodyHandlers.ofString()
        );
    }

}
