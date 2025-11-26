package com.example.one.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiService {

//    @Retryable(
//            value = { RuntimeException.class },
//            maxAttempts = 3,
//            backoff = @Backoff(delay = 1000)
//    )
//    public String tesRuntimeExceptionRetryAndFallback() {
//        log.info("Memanggil API...");
//        throw new RuntimeException("API down!");
//    }
//
//    @Retryable(
//            value = { CustomOneException.class },
//            maxAttempts = 3,
//            backoff = @Backoff(delay = 1000)
//    )
//    public String tesCustomOneExceptionRetryAndFallback() {
//        log.info("Memanggil API...");
//        throw new CustomOneException("API down!");
//    }
//
//    @Retryable(
//            value = { ArithmeticException.class },
//            maxAttempts = 3,
//            backoff = @Backoff(delay = 1000)
//    )
//    public int tesArithmeticExceptionRetryAndFallback() {
//        log.info("Memanggil API...");
//
//        int result;
//        try {
//            int a = 0;
//            int b = 0;
//
//            result = a / b; // Melempar ArithmeticException
//        } catch (ArithmeticException e) {
//            throw e;
//        }
//        return result;
//    }
//
//    @Recover
//    public String recover(RuntimeException e) {
//        return "Fallback response karena API gagal! (RuntimeException is thrown)";
//    }
//
//    @Recover
//    public String recover(CustomOneException e) {
//        return "Fallback response karena API gagal! (CustomOneException is thrown)";
//    }
//
//    @Recover
//    public int recover(ArithmeticException e) {
//        return -1; // nilai fallback
//    }


    @Retry(name = "call-external-api")
    @CircuitBreaker(name = "call-external-api", fallbackMethod = "myFallbackMethod")
    public String callExternalApi() {
        log.info("Calling API...");
        throw new RuntimeException("API External down!");
    }

    // fallback harus punya parameter Throwable di terakhir
    public String myFallbackMethod(Throwable throwable) {
        return "Fallback: API sedang bermasalah!";
    }
}
