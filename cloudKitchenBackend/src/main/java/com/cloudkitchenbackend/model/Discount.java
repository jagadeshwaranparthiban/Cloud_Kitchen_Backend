package com.cloudkitchenbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long discountId;
    private String discountCode;
    private DiscountType discountType;
    private double discountValue;
    private double minLevel;
    private DiscountStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime issuedAt;
    private int maxUsage;
    private int currentUsage;

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

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public double getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(double minLevel) {
        this.minLevel = minLevel;
    }

    public DiscountStatus getStatus() {
        return status;
    }

    public void setStatus(DiscountStatus status) {
        this.status = status;
    }

    public int getMaxUsage() {
        return maxUsage;
    }

    public void setMaxUsage(int maxUsage) {
        this.maxUsage = maxUsage;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public int getCurrentUsage() {
        return currentUsage;
    }

    public void setCurrentUsage(int currentUsage) {
        this.currentUsage = currentUsage;
    }
}
