package ru.yandex.yandexlavka.filter.throttling;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter limiting the requests per endpoints (URI)
 */
@Component
@SuppressWarnings("UnstableApiUsage")
public class ThrottlingFilter implements Filter {

    @Value("${requests-per-second-for-handler}")
    private int requestsPerSecond;

    private final Map<String, RateLimiter> rateLimiterMap = new HashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
        String requestURI = httpServletRequest.getRequestURI();
        RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(requestURI, this::createNewRateLimiter);
        if (!rateLimiter.tryAcquire()) {
            printTooManyRequestsResponse(response);
            return;
        }
        chain.doFilter(request, response);
    }

    private RateLimiter createNewRateLimiter(String requestURI) {
        return RateLimiter.create(requestsPerSecond);
    }

    private static final String TOO_MANY_REQUESTS_RESPONSE_BODY = "{}";

    private void printTooManyRequestsResponse(ServletResponse response) throws IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        httpServletResponse.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        httpServletResponse.getOutputStream().print(TOO_MANY_REQUESTS_RESPONSE_BODY);
    }
}
