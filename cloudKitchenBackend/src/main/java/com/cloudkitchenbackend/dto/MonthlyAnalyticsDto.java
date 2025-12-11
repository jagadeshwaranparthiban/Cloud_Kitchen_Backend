package com.cloudkitchenbackend.dto;

import java.time.LocalDate;

public record MonthlyAnalyticsDto(String month, Long orderCount) {
}
