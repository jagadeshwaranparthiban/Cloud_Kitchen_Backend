package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.dto.*;
import com.cloudkitchenbackend.exception.*;
import com.cloudkitchenbackend.model.*;
import com.cloudkitchenbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private OrdersRepo ordersRepo;
    private ItemRepo itemRepo;
    private OrderItemRepo orderItemRepo;
    private UserRepo userRepo;
    private EmailService emailService;
    private DiscountService discountService;

    @Autowired
    public OrderService(OrdersRepo ordersRepo, ItemRepo itemRepo, OrderItemRepo orderItemRepo,
                        UserRepo userRepo, EmailService emailService, DiscountService discountService){
        this.ordersRepo=ordersRepo;
        this.itemRepo=itemRepo;
        this.orderItemRepo=orderItemRepo;
        this.userRepo=userRepo;
        this.emailService=emailService;
        this.discountService=discountService;
    }

    public OrderResponseDto createOrder(OrderRequestDto requestedOrder) {
        String custName=requestedOrder.getCustomerName();
        Optional<Users> customer=userRepo.findByUserName(custName);
        if(customer.isEmpty()) throw new UserNotFoundException("Invalid user!");

        Orders order=new Orders();
        order.setCustomerName(requestedOrder.getCustomerName());
        List<OrderItem> orderItemList = new ArrayList<>();
        double total = 0.0;

        for(ItemInfoDto itemInfo: requestedOrder.getItems()){
            Item item = itemRepo.findByItemName(itemInfo.getItemName())
                    .orElseThrow(() -> new ItemNotFoundException("Item not found: " + itemInfo.getItemName()));
            if(!item.isAvailable()) throw new ItemUnavailableException("Item "+item.getItemName()+" is currently unavailable");

            OrderItem orderItem=new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(itemInfo.getQty());
            orderItem.setItemTotalCost(item.getPrice()*itemInfo.getQty());

            total += orderItem.getItemTotalCost();
            orderItemList.add(orderItem);
        }

        double tax=total*0.05;
        order.setOrderItems(orderItemList);
        order.setTotalCost(total+tax);
        order.setTax(tax);
        order.setOrderTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        ordersRepo.save(order);

        emailService.sendOrderConfirmationMail(customer.get().getEmail(),
                "ORDER CONFIRMATION",
                "Order placed successfully.\n\n Your order ID: "+order.getOrderId()+". Use this to view your order status.");

        return new OrderResponseDto(
                order.getOrderId(),
                order.getTotalCost(),
                OrderStatus.PENDING,
                order.getTax(),
                discountService.isEligibleForDiscount(order.getTotalCost())
        );
    }

    public SuccessfulResponse cancelOrder(OrderCancelRequest cancel_request){
        Optional<Orders> order=ordersRepo.findByOrderId(cancel_request.getOrderId());
        if(order.isEmpty()){
            throw new OrderNotFoundException("Order ID: "+cancel_request.getOrderId()+" not found.");
        }if(!order.get().getCustomerName().equals(cancel_request.getCustomerName())){
            throw new InvalidCustomerException("Invalid Customer");
        }
        Optional<Users> costumer=userRepo.findByUserName(cancel_request.getCustomerName());
        Orders cancelOrder=order.get();
        double refund=refundAmount(cancelOrder.getTotalCost());

//        orderItemRepo.deleteByOrderId(cancelOrderId);
//        ordersRepo.delete(order.get());
        cancelOrder.setOrderStatus(OrderStatus.CANCELLED);
        ordersRepo.save(cancelOrder);
        emailService.sendOrderCancellationMail(costumer.get().getEmail(), refund);
        return new SuccessfulResponse("Order cancelled successfully. Your refund: Rs."+refund);
    }

    public double refundAmount(double amt){
        return amt-(0.1*amt);
    }

    public OrdersDisplayDto getOrder(long orderId) {
        Optional<Orders> order=ordersRepo.findByOrderId(orderId);
        if(order.isEmpty()) throw new OrderNotFoundException("Order with id: "+orderId+" not found");
        List<OrderItem> orderItems=order.get().getOrderItems();

        OrdersDisplayDto response=new OrdersDisplayDto();
        response.setOrderId(orderId);
        response.setTotalCost(order.get().getTotalCost());
        response.setTax(order.get().getTax());
        response.setStatus(order.get().getOrderStatus());

        List<ItemInfoDisplayDto> itemList=new ArrayList<>();
        for(OrderItem orderItem: orderItems){
            Item item=orderItem.getItem();
            ItemInfoDisplayDto itemInfo=new ItemInfoDisplayDto();
            itemInfo.setItemName(item.getItemName());
            itemInfo.setQty(orderItem.getQuantity());
            itemInfo.setCost(orderItem.getItemTotalCost());
            itemList.add(itemInfo);
        }
        response.setItems(itemList);
        return response;
    }

    public DiscountApplyResponseDto applyDiscount(long orderId, String discountCode) {
        Optional<Orders> orderResponse=ordersRepo.findByOrderId(orderId);
        if(orderResponse.isEmpty()){
            throw new OrderNotFoundException("Order with ID: "+orderId+" not found.");
        }

        Orders order=orderResponse.get();
        Discount discount=discountService.getDiscount(discountCode);
        if(discount.getStatus()==DiscountStatus.INACTIVE || discount.getStatus()==DiscountStatus.EXPIRED){
            throw new InvalidDiscountException("Discount currently inactive or expired.");
        }
        if(discount.getCurrentUsage() >= discount.getMaxUsage()){
            discountService.setDiscountStatus(discount.getDiscountId(), DiscountStatus.EXPIRED);
            throw new DiscountReachedMaximumUsersException("Discount reached maximum use limit");
        }
        if(discount.getMinLevel()>order.getTotalCost()){
            throw new InvalidDiscountException("Discount code cannot be applied for this order");
        }

        DiscountApplyResponseDto res=new DiscountApplyResponseDto();
        double oldCost=order.getTotalCost();
        double newCost;
        res.setOldPrice(oldCost);
        res.setDiscountCode(discountCode);
        res.setOrderId(orderId);
        res.setDiscountCode(discountCode);
        if(discount.getDiscountType()==DiscountType.FLAT){
            newCost=oldCost-discount.getDiscountValue();
        }else{
            newCost=oldCost - (oldCost*(discount.getDiscountValue()/100));
        }

        res.setNewPrice(newCost);
        res.setStatus("SUCCESSFULL");
        order.setTotalCost(newCost);
        order.setDiscountCode(discountCode);
        ordersRepo.save(order);
        discountService.incrementUsage(discount.getDiscountId());
        return res;
    }
}
