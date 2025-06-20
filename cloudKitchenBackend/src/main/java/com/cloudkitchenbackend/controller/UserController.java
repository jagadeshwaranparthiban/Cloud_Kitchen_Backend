package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.dto.NewUserDto;
import com.cloudkitchenbackend.dto.SuccessfulResponse;
import com.cloudkitchenbackend.dto.UserLoginDto;
import com.cloudkitchenbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService){
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
}
