package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.dto.PaymentOrderRequestDto;
import com.cloudkitchenbackend.dto.PaymentVerificationRequestDto;
import com.cloudkitchenbackend.exception.OrderNotFoundException;
import com.cloudkitchenbackend.exception.PaymentFailedException;
import com.cloudkitchenbackend.model.OrderStatus;
import com.cloudkitchenbackend.model.Orders;
import com.cloudkitchenbackend.model.Payment;
import com.cloudkitchenbackend.model.PaymentStatus;
import com.cloudkitchenbackend.repository.OrdersRepo;
import com.cloudkitchenbackend.repository.PaymentRepo;
import com.cloudkitchenbackend.repository.UserRepo;
import com.cloudkitchenbackend.util.PaymentUtil;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
public class PaymentService {

    private OrdersRepo ordersRepo;
    private PaymentRepo paymentRepo;
    private UserRepo userRepo;
    private PaymentUtil paymentUtil;

    public PaymentService(OrdersRepo ordersRepo,
                          PaymentRepo paymentRepo,
                          UserRepo userRepo,
                          PaymentUtil paymentUtil) {
        this.ordersRepo = ordersRepo;
        this.paymentRepo = paymentRepo;
        this.userRepo = userRepo;
        this.paymentUtil=paymentUtil;
    }

    @Value("${razorpay.key.id}")
    private String key;

    @Value("${razorpay.key.secret}")
    private String secret;

    public String createPaymentOrder(PaymentOrderRequestDto paymentRequest) throws RazorpayException {
        Optional<Orders> requestOrder=ordersRepo.findByOrderId(paymentRequest.getOrderId());
        if(requestOrder.isEmpty()){
            throw new OrderNotFoundException("Order with id:"+paymentRequest.getOrderId()+" not found");
        }
        Orders paymentOrder=requestOrder.get();
        double finalCost=paymentOrder.getTotalCost();
        String userId = userRepo.findUserIdByUserName(paymentOrder.getCustomerName());

        RazorpayClient razorpayClient = new RazorpayClient(key, secret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", finalCost*100);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", paymentRequest.getPaymentReciept());
        orderRequest.put("payment_capture", true);

        Payment newPayment = new Payment();
        newPayment.setPaymentId(paymentRequest.getPaymentReciept());
        newPayment.setOrderId(paymentOrder.getOrderId());
        newPayment.setUserId(userId);
        newPayment.setPaymentMethod(paymentRequest.getPaymentMethod());
        newPayment.setPaymentStatus(PaymentStatus.PENDING);
        newPayment.setAmount(finalCost);

        paymentRepo.save(newPayment);

        Order order = razorpayClient.orders.create(orderRequest);
        return order.toString();
    }

    public String verifyPaymentOrder(PaymentVerificationRequestDto request){
        try{
            String data=request.getRazorpayOrderId()+"|"+request.getRazorpayPaymentId();
            String actualSignature = PaymentUtil.hmacSha256(data, secret);

            Payment oldPaymentStatus = paymentRepo.findByOrderId(request.getOrderId())
                    .orElseThrow(() -> new PaymentFailedException("Payment information not found"));
            Orders order = ordersRepo.findByOrderId(request.getOrderId())
                    .orElseThrow(() -> new OrderNotFoundException("Order not found"));

            RazorpayClient razorpayClient = new RazorpayClient(key, secret);
            com.razorpay.Payment payment = razorpayClient.payments.fetch(request.getRazorpayPaymentId());

            oldPaymentStatus.setPaymentId(request.getRazorpayPaymentId());
            oldPaymentStatus.setGatewayReferenceId(payment.get("id"));
            oldPaymentStatus.setPaymentMethod(payment.get("method"));

            if (actualSignature.equals(request.getRazorpaySignature())) {
                oldPaymentStatus.setPaymentTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                oldPaymentStatus.setPaymentStatus(PaymentStatus.SUCCESS);
                order.setOrderStatus(OrderStatus.CONFIRMED);
                paymentRepo.save(oldPaymentStatus);
                ordersRepo.save(order);
            } else {
                oldPaymentStatus.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepo.save(oldPaymentStatus);
                throw new PaymentFailedException("Payment failed due to an error");
            }
            return "Payment successfull! Your order with id: "+order.getOrderId()+" has been confirmed.";
        }catch (Exception e) {
            throw new PaymentFailedException(e.getMessage());
        }
    }
}
