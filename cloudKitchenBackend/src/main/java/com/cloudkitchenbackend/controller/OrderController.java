package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.dto.*;
import com.cloudkitchenbackend.exception.PaymentFailedException;
import com.cloudkitchenbackend.model.Orders;
import com.cloudkitchenbackend.service.EmailService;
import com.cloudkitchenbackend.service.OrderService;
import com.razorpay.RazorpayException;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order")
public class OrderController {
    private OrderService orderService;
    private EmailService emailService;

    @Autowired
    public OrderController(OrderService orderService, EmailService emailService){
        this.orderService=orderService;
        this.emailService=emailService;
    }

//    @PostMapping("/send_mail")
//    public ResponseEntity<String> sendEmail(){
//        String res=emailService.sendOrderConfirmationMail("230701120@rajalakshmi.edu.in",
//                "CKitchen: Order confirmation", "Order recieved successfully!");
//        return ResponseEntity.ok(res);
//    }

    @PostMapping("/place")
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody OrderRequestDto order){
        try{
            return ResponseEntity.ok(orderService.createOrder(order));
        }catch(RazorpayException ex){
            throw new PaymentFailedException(ex.getMessage());
        }
    }

    @PutMapping("/cancel")
    public ResponseEntity<SuccessfulResponse> cancelOrder(@RequestBody OrderCancelRequest cancel_request){
        return ResponseEntity.ok(orderService.cancelOrder(cancel_request));
    }

    @GetMapping("/view")
    public ResponseEntity<OrdersDisplayDto> viewOrders(@RequestParam long orderId){
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PostMapping("/add_discount")
    public ResponseEntity<DiscountApplyResponseDto> addDiscount(@RequestParam long orderId, @RequestParam String discountCode){
        return ResponseEntity.ok(orderService.applyDiscount(orderId, discountCode));
    }
}
