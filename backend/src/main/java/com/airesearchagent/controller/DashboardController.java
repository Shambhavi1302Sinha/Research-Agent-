package com.airesearchagent.controller;

import com.airesearchagent.dto.DashboardDtos;
import com.airesearchagent.entity.User;
import com.airesearchagent.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardDtos.DashboardResponse dashboard(@AuthenticationPrincipal User user) {
        return dashboardService.getDashboard(user);
    }
}
