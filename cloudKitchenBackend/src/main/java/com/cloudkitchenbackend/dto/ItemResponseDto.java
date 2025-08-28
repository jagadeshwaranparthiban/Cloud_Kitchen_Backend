package com.cloudkitchenbackend.dto;


public record ItemResponseDto(String itemName, String desc, String image, Double price, boolean isVeg) {
}
