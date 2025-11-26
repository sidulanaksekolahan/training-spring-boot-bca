package com.example.one.controller;

import com.example.one.entity.Product;
import com.example.one.service.ApiService;
import com.example.one.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private ApiService apiService;

    private ProductService productService;

    @Autowired
    public ProductController(ApiService apiService, ProductService productService) {
        this.apiService = apiService;
        this.productService = productService;
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<Object> getAllProducts() {
        log.info("getAllProducts");

        List<Product> productList = productService.getAllProducts();

        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/callExternalApiRetryAndFallback")
    public ResponseEntity<Object> tesRuntimeExceptionRetryAndFallback(HttpServletRequest httpRequest) {
        String data = apiService.callExternalApi(httpRequest);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping("/saveProduct")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        log.info("saveProduct");

        Product result = productService.saveProduct(product);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
