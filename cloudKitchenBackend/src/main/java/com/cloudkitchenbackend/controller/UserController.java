package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.dto.*;
import com.cloudkitchenbackend.model.Discount;
import com.cloudkitchenbackend.model.Item;
import com.cloudkitchenbackend.service.DiscountService;
import com.cloudkitchenbackend.service.MenuService;
import com.cloudkitchenbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private UserService userService;
    private MenuService menuService;
    private DiscountService discountService;

    @Autowired
    public UserController(UserService userService, MenuService menuService, DiscountService discountService){
        this.menuService=menuService;
        this.userService=userService;
        this.discountService=discountService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> userLogin(@RequestBody UserLoginDto loginCred){
        return ResponseEntity.ok(userService.authUser(loginCred));
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessfulResponse> registerNewUser(@RequestBody NewUserDto userCred){
        return ResponseEntity.ok(userService.RegisterUser(userCred));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> getRefreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return ResponseEntity.ok(userService.getNewRefreshToken(refreshTokenRequestDto.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessfulResponse> logOutUser(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return ResponseEntity.ok(userService.logOutUser(refreshTokenRequestDto.refreshToken()));
    }

    @GetMapping("/view")
    public ResponseEntity<Item> getItem(@RequestParam String name){
        return ResponseEntity.ok(menuService.getItem(name));
    }

    @GetMapping("/menu")
    public ResponseEntity<List<ItemResponseDto>> getMenu(){
        return ResponseEntity.ok(menuService.getItems());
    }

    @GetMapping("/get_discounts")
    public ResponseEntity<ValidDiscountsDto> getValidDiscounts(@RequestParam double orderCost) {
        List<Discount> discount = discountService.getBestDiscount(orderCost);
        List<DiscountInfoDto> validDiscounts = discount.stream().map(d -> {
            return new DiscountInfoDto(d.getDiscountCode(),d.getDiscountType(), d.getDiscountValue());
        }).toList();
        return ResponseEntity.ok(new ValidDiscountsDto(validDiscounts));
    }
}
