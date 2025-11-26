package com.example.one.controller;

//import com.example.one.service.ApiService;
import com.example.one.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private ApiService apiService;

    @Autowired
    public ProductController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<Object> getAllProducts() {
        log.info("getAllProducts");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "some product");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/callExternalApiRetryAndFallback")
    public ResponseEntity<Object> tesRuntimeExceptionRetryAndFallback() {
        String data = apiService.callExternalApi();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
