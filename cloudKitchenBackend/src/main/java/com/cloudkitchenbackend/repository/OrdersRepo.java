package com.cloudkitchenbackend.repository;

import com.cloudkitchenbackend.dto.RevenueMapperDto;
import com.cloudkitchenbackend.dto.WeeklyAnalyticsDto;
import com.cloudkitchenbackend.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepo extends JpaRepository<Orders,Long> {
    Optional<Orders> findByOrderId(long orderId);
    void deleteByOrderId(long cancelOrderId);

    @Query("SELECT COUNT(o.orderId) AS totalOrders, SUM(o.totalCost) AS totalRevenue " +
            "FROM Orders o " +
            "WHERE o.orderTime >= :dayStart AND o.orderTime < :dayEnd")
    List<Object[]> findDailyAnalytics(LocalDateTime dayStart, LocalDateTime dayEnd);

    @Query(value = """
        SELECT EXTRACT(MONTH FROM order_time)::int AS month_num, COUNT(*) AS cnt
        FROM orders
        WHERE EXTRACT(YEAR FROM order_time) = EXTRACT(YEAR FROM CURRENT_DATE)
        GROUP BY month_num
        ORDER BY month_num
        """, nativeQuery = true)
    List<Object[]> findMonthlyAnalytics();

    @Query("""
            SELECT new com.cloudkitchenbackend.dto.RevenueMapperDto(oi.item.itemName, SUM(oi.quantity), SUM(oi.itemTotalCost))
            FROM OrderItem oi
            JOIN oi.item i
            GROUP BY i.itemName
            ORDER BY SUM(oi.itemTotalCost) DESC
            """)
    List<RevenueMapperDto> findTotalRevenueByItem();
}
