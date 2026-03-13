package com.ecommerce.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/endpoints")
    public Map<String, Object> getEndpoints() {
        return Map.of(
            "message", "Available endpoints",
            "auth", List.of(
                "/api/auth/register",
                "/api/auth/login", 
                "/api/auth/forgot-password",
                "/api/auth/verify-otp",
                "/api/auth/reset-password"
            ),
            "status", "OK"
        );
    }
}
