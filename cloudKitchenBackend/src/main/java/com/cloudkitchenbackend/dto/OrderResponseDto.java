package com.cloudkitchenbackend.dto;

import com.cloudkitchenbackend.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponseDto {
    private long orderId;
    private double totalCost;
    private OrderStatus status;
    private double tax;
    private boolean isEligibleForDiscount;

    public OrderResponseDto(long orderId, double totalCost, OrderStatus status, double tax, boolean isEligibleForDiscount) {
        this.orderId = orderId;
        this.totalCost = totalCost;
        this.status = status;
        this.tax = tax;
        this.isEligibleForDiscount = isEligibleForDiscount;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus (OrderStatus status) {
        this.status = status;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public boolean isEligibleForDiscount() {
        return isEligibleForDiscount;
    }

    public void setEligibleForDiscount(boolean eligibleForDiscount) {
        isEligibleForDiscount = eligibleForDiscount;
    }
}
