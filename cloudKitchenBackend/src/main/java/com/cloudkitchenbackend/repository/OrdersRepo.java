package com.cloudkitchenbackend.repository;

import com.cloudkitchenbackend.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepo extends JpaRepository<Orders,Long> {
    Optional<Orders> findByOrderId(long orderId);
    void deleteByOrderId(long cancelOrderId);
}
