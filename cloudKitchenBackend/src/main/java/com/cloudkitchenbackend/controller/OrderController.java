package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.dto.OrderCancelRequest;
import com.cloudkitchenbackend.dto.OrderRequestDto;
import com.cloudkitchenbackend.dto.OrderResponseDto;
import com.cloudkitchenbackend.model.Orders;
import com.cloudkitchenbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {
    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService=orderService;
    }

    @PostMapping("/place_order")
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody OrderRequestDto order){
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @PutMapping("/cancel_order")
    public ResponseEntity<String> cancelOrder(@RequestBody OrderCancelRequest cancel_request){
        return ResponseEntity.ok(orderService.cancelOrder(cancel_request));
    }
}
