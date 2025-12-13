package com.cloudkitchenbackend.dto;


import com.cloudkitchenbackend.model.ItemCategory;

public record ItemResponseDto(String itemName, String desc, String image, Double price, boolean isVeg, ItemCategory category) {
}
