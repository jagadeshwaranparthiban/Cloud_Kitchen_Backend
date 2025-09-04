package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.dto.ItemResponseDto;
import com.cloudkitchenbackend.dto.NewUserDto;
import com.cloudkitchenbackend.dto.SuccessfulResponse;
import com.cloudkitchenbackend.dto.UserLoginDto;
import com.cloudkitchenbackend.model.Item;
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

    @Autowired
    public UserController(UserService userService, MenuService menuService){
        this.menuService=menuService;
        this.userService=userService;
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessfulResponse> userLogin(@RequestBody UserLoginDto loginCred){
        return ResponseEntity.ok(userService.authUser(loginCred));
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessfulResponse> registerNewUser(@RequestBody NewUserDto userCred){
        return ResponseEntity.ok(userService.RegisterUser(userCred));
    }

    @GetMapping("/view")
    public ResponseEntity<Item> getItem(@RequestParam String name){
        return ResponseEntity.ok(menuService.getItem(name));
    }

    @GetMapping("/menu")
    public ResponseEntity<List<ItemResponseDto>> getMenu(){
        return ResponseEntity.ok(menuService.getItems());
    }
}
