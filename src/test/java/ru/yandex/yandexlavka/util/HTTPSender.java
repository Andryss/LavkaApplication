package ru.yandex.yandexlavka.util;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

@Component
public class HTTPSender {

    @SneakyThrows
    public HttpResponse<String> sendPostRequest(HttpClient client, String url, String body) {
        return client.send(
                HttpRequest.newBuilder(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(BodyPublishers.ofString(body))
                        .build(),
                BodyHandlers.ofString()
        );
    }

    @SneakyThrows
    public HttpResponse<String> sendGetRequest(HttpClient client, String url) {
        return client.send(
                HttpRequest.newBuilder(URI.create(url))
                        .GET()
                        .build(),
                BodyHandlers.ofString()
        );
    }

}
