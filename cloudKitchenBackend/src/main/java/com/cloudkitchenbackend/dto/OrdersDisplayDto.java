package com.cloudkitchenbackend.dto;

import com.cloudkitchenbackend.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDisplayDto {
    private long orderId;
    private List<ItemInfoDisplayDto> items;
    private double totalCost;
    private double tax;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public List<ItemInfoDisplayDto> getItems() {
        return items;
    }

    public void setItems(List<ItemInfoDisplayDto> items) {
        this.items = items;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }
}
