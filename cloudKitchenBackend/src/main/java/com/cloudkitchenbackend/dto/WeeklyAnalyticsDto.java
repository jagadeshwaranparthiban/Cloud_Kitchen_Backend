package com.cloudkitchenbackend.dto;

import java.math.BigDecimal;
import java.time.DayOfWeek;

public record WeeklyAnalyticsDto(DayOfWeek weekday, double totalRevenue) {}

