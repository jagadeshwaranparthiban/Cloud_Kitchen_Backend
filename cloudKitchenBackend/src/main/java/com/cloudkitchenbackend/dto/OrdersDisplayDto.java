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
    private List<ItemInfoDto> items;
    private double totalCost;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public List<ItemInfoDto> getItems() {
        return items;
    }

    public void setItems(List<ItemInfoDto> items) {
        this.items = items;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
