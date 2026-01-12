package com.cloudkitchenbackend.dto;

import com.cloudkitchenbackend.model.DiscountType;

public record DiscountInfoDto(String discountCode, DiscountType discountType, double discountValue) {
}
