package com.example.one.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/welcome")
public class HomeController {

    @GetMapping("/home")
    public ResponseEntity<Object> home() {

        log.info("home controller");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to kubernetes");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
