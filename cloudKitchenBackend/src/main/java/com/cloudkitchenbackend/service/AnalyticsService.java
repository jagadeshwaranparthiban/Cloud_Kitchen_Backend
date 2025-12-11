package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.dto.MonthlyAnalyticsDto;
import com.cloudkitchenbackend.dto.WeeklyAnalyticsDto;
import com.cloudkitchenbackend.repository.ItemRepo;
import com.cloudkitchenbackend.repository.OrdersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AnalyticsService {
    private OrdersRepo ordersRepo;
    private ItemRepo itemRepo;

    @Autowired
    public AnalyticsService(OrdersRepo ordersRepo, ItemRepo itemRepo) {
        this.ordersRepo = ordersRepo;
        this.itemRepo = itemRepo;
    }

    public ArrayList<WeeklyAnalyticsDto> getWeeklyAnalytics() {
        ArrayList<WeeklyAnalyticsDto> analytics = null;
        try {
            analytics = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            for (int i = 6; i >= 0; i--) {
                LocalDateTime dayStart = now.minusDays(i).withHour(0).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime dayEnd = dayStart.plusDays(1);

                List<Object[]> dailyData = ordersRepo.findDailyAnalytics(dayStart, dayEnd);

                long totalOrders = 0L;
                double totalRevenue = 0.0;

                for (Object[] data : dailyData) {
                    // data[0] = count, data[1] = sum
                    Number ordersCount = (data[0] instanceof Number) ? (Number) data[0] : null;
                    Number revenueSum  = (data[1] instanceof Number) ? (Number) data[1] : null;

                    if (ordersCount != null) {
                        totalOrders += ordersCount.longValue();
                    }
                    if (revenueSum != null) {
                        // use doubleValue() or BigDecimal -> double
                        totalRevenue += revenueSum.doubleValue();
                    }
                }

                WeeklyAnalyticsDto dailyAnalytics = new WeeklyAnalyticsDto(dayStart.toLocalDate().getDayOfWeek(), totalRevenue);
                analytics.add(dailyAnalytics);
            }
        } catch (Exception ex) {
            System.out.println("Error occured with analytics service"+ex.getMessage());
        }
        return analytics;
    }

    public List<MonthlyAnalyticsDto> getMonthlyAnalytics() {
        try{
            List<Object[]> rows = ordersRepo.findMonthlyAnalytics();

            // convert DB rows to map: monthNumber -> count
            Map<Integer, Long> counts = new HashMap<>();
            for (Object[] row : rows) {
                Integer monthNum = ((Number) row[0]).intValue();   // 1..12
                Long cnt = ((Number) row[1]).longValue();
                counts.put(monthNum, cnt);
            }

            // Create a list of 12 MonthCount entries (JANUARY..DECEMBER) with zero defaults
            return IntStream.rangeClosed(1, 12)
                    .mapToObj(i -> {
                        String monthName = Month.of(i).name(); // e.g. "JANUARY"
                        long orderCount = counts.getOrDefault(i, 0L);
                        return new MonthlyAnalyticsDto(monthName, orderCount);
                    })
                    .collect(Collectors.toList());
        }catch(Exception ex) {
            System.out.println("Error occured with analytics service"+ex.getMessage());
            return null;
        }
    }
}
