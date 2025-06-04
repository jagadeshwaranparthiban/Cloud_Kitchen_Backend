package com.cloudkitchenbackend.dto;

import com.cloudkitchenbackend.model.DiscountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewDiscountDto {
    private long discountId;
    private String discountCode;
    private int currentUsage;
    private int maxusage;
    private DiscountStatus status;

    public long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(long discountId) {
        this.discountId = discountId;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public int getCurrentUsage() {
        return currentUsage;
    }

    public void setCurrentUsage(int currentUsage) {
        this.currentUsage = currentUsage;
    }

    public int getMaxusage() {
        return maxusage;
    }

    public void setMaxusage(int maxusage) {
        this.maxusage = maxusage;
    }

    public DiscountStatus getStatus() {
        return status;
    }

    public void setStatus(DiscountStatus status) {
        this.status = status;
    }
}
