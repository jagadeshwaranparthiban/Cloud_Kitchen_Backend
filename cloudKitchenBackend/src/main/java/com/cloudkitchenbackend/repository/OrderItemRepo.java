package com.cloudkitchenbackend.repository;

import com.cloudkitchenbackend.model.OrderItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") long orderId);
}
