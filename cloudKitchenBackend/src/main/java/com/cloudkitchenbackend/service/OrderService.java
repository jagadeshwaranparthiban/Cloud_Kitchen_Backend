package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.dto.ItemInfoDto;
import com.cloudkitchenbackend.dto.OrderCancelRequest;
import com.cloudkitchenbackend.dto.OrderRequestDto;
import com.cloudkitchenbackend.dto.OrderResponseDto;
import com.cloudkitchenbackend.exception.ItemNotFoundException;
import com.cloudkitchenbackend.exception.OrderNotFoundException;
import com.cloudkitchenbackend.model.Item;
import com.cloudkitchenbackend.model.OrderItem;
import com.cloudkitchenbackend.model.Orders;
import com.cloudkitchenbackend.repository.ItemRepo;
import com.cloudkitchenbackend.repository.OrderItemRepo;
import com.cloudkitchenbackend.repository.OrdersRepo;
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

    @Autowired
    public OrderService(OrdersRepo ordersRepo, ItemRepo itemRepo, OrderItemRepo orderItemRepo){
        this.ordersRepo=ordersRepo;
        this.itemRepo=itemRepo;
        this.orderItemRepo=orderItemRepo;
    }

    public OrderResponseDto createOrder(OrderRequestDto requestedOrder) {

        Orders order=new Orders();
        order.setCustomerName(requestedOrder.getCustomerName());
        List<OrderItem> orderItemList = new ArrayList<>();
        double total = 0.0;
        for(ItemInfoDto itemInfo: requestedOrder.getItems()){
            Item item = itemRepo.findByItemName(itemInfo.getItemName())
                    .orElseThrow(() -> new ItemNotFoundException("Item not found: " + itemInfo.getItemName()));
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
        }
        long cancelOrderId=order.get().getOrderId();
        double refund=refundAmount(cancelOrderId);

        orderItemRepo.deleteByOrderId(cancelOrderId);
        ordersRepo.delete(order.get());
        return "Order cancelled successfully. Your refund: Rs."+refund;
    }

    public double refundAmount(long orderId){
        Optional<Orders> order=ordersRepo.findByOrderId(orderId);
        double amt=order.get().getTotalCost();
        return amt-(0.1*amt);
    }
}
