package com.e1i5.stackOverflow.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthCheck {
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("healthCheck passed");
    }
}
