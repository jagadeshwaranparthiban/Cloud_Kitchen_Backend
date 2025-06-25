package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.dto.PaymentOrderRequestDto;
import com.cloudkitchenbackend.dto.PaymentVerificationRequestDto;
import com.cloudkitchenbackend.dto.SuccessfulResponse;
import com.cloudkitchenbackend.exception.PaymentFailedException;
import com.cloudkitchenbackend.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<SuccessfulResponse> createOrder(@RequestBody PaymentOrderRequestDto paymentRequest){
        try{
            String paymentOrderResponse=paymentService.createPaymentOrder(paymentRequest);
            return ResponseEntity.ok(new SuccessfulResponse(paymentOrderResponse));
        }catch(RazorpayException ex){
            throw new PaymentFailedException(ex.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<SuccessfulResponse> verifyOrder(@RequestBody PaymentVerificationRequestDto request){
        return ResponseEntity.ok(new SuccessfulResponse(paymentService.verifyPaymentOrder(request)));
    }
}
