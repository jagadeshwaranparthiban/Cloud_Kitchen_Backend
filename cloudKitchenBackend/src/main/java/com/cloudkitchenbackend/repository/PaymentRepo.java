package com.cloudkitchenbackend.repository;

import com.cloudkitchenbackend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrderId(long orderId);
}
