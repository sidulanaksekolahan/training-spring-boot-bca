package com.example.one.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiService {

    @Retry(name = "call-external-api")
    @CircuitBreaker(name = "call-external-api", fallbackMethod = "myFallbackMethod")
    public String callExternalApi(HttpServletRequest httpRequest) {
        log.info("Calling API...");
        log.info("HttpMethod: {}", httpRequest.getMethod());
        throw new RuntimeException("API External down!");
    }

    // fallback harus punya parameter Throwable di terakhir
    public String myFallbackMethod(HttpServletRequest httpRequest, Throwable throwable) {
        log.info("URL: {}", httpRequest.getRequestURI());
        return "Fallback: API sedang bermasalah!";
    }
}
