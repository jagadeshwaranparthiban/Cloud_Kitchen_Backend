package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.dto.MonthlyAnalyticsDto;
import com.cloudkitchenbackend.dto.WeeklyAnalyticsDto;
import com.cloudkitchenbackend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/orders/weekly")
    public ResponseEntity<ArrayList<WeeklyAnalyticsDto>> getWeeklyAnalytics() {
        return ResponseEntity.ok(analyticsService.getWeeklyAnalytics());
    }

    @GetMapping("/orders/monthly")
    public ResponseEntity<Map<String,List<MonthlyAnalyticsDto>>> getDailyAnalytics() {
        return ResponseEntity.ok(Map.of("data", analyticsService.getMonthlyAnalytics()));
    }
}
