package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.dto.OrderResponseDto;
import com.cloudkitchenbackend.dto.PaymentRequestDto;
import com.cloudkitchenbackend.model.Orders;
import com.cloudkitchenbackend.repository.OrdersRepo;
import com.cloudkitchenbackend.repository.PaymentRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private OrdersRepo ordersRepo;
    private PaymentRepo paymentRepo;

    public PaymentService(OrdersRepo ordersRepo, PaymentRepo paymentRepo) {
        this.ordersRepo = ordersRepo;
        this.paymentRepo = paymentRepo;
    }


}
