package com.airesearchagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "application", "AI Research Agent Backend",
                "status", "running",
                "health", "/api/health",
                "auth", "/api/auth/login"
        );
    }
}
