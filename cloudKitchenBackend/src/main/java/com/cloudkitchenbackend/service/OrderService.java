package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.dto.*;
import com.cloudkitchenbackend.exception.*;
import com.cloudkitchenbackend.model.Item;
import com.cloudkitchenbackend.model.OrderItem;
import com.cloudkitchenbackend.model.Orders;
import com.cloudkitchenbackend.model.Users;
import com.cloudkitchenbackend.repository.ItemRepo;
import com.cloudkitchenbackend.repository.OrderItemRepo;
import com.cloudkitchenbackend.repository.OrdersRepo;
import com.cloudkitchenbackend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private OrdersRepo ordersRepo;
    private ItemRepo itemRepo;
    private OrderItemRepo orderItemRepo;
    private UserRepo userRepo;

    @Autowired
    public OrderService(OrdersRepo ordersRepo, ItemRepo itemRepo, OrderItemRepo orderItemRepo,
                        UserRepo userRepo){
        this.ordersRepo=ordersRepo;
        this.itemRepo=itemRepo;
        this.orderItemRepo=orderItemRepo;
        this.userRepo=userRepo;
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

        ordersRepo.save(order);
        return new OrderResponseDto(
                order.getOrderId(),
                order.getTotalCost(),
                "Order placed successfully",
                order.getTax()
        );
    }

    public String cancelOrder(OrderCancelRequest cancel_request){
        Optional<Orders> order=ordersRepo.findByOrderId(cancel_request.getOrderId());
        if(order.isEmpty()){
            throw new OrderNotFoundException("Order ID: "+cancel_request.getOrderId()+" not found.");
        }if(!order.get().getCustomerName().equals(cancel_request.getCustomerName())){
            throw new InvalidCustomerException("Invalid Customer");
        }
        long cancelOrderId=order.get().getOrderId();
        double refund=refundAmount(order.get());

        orderItemRepo.deleteByOrderId(cancelOrderId);
        ordersRepo.delete(order.get());
        return "Order cancelled successfully. Your refund: Rs."+refund;
    }

    public double refundAmount(Orders order){
        double amt=order.getTotalCost();
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
}
