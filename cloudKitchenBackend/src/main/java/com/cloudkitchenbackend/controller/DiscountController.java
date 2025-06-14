package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.dto.ViewDiscountDto;
import com.cloudkitchenbackend.model.Discount;
import com.cloudkitchenbackend.model.DiscountStatus;
import com.cloudkitchenbackend.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discount")
public class DiscountController {

    private DiscountService discountService;

    @Autowired
    public DiscountController(DiscountService discountService){
        this.discountService=discountService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addDiscountCode(@RequestBody Discount discount){
        return ResponseEntity.ok(discountService.addDiscountCode(discount));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeDiscountCode(@RequestParam String code){
        return ResponseEntity.ok(discountService.removeDiscountCode(code));
    }

    @GetMapping("/view")
    public ResponseEntity<ViewDiscountDto> viewDiscountDetails(@RequestParam long id){
        return ResponseEntity.ok(discountService.viewDiscount(id));
    }

    @PutMapping("/set")
    public ResponseEntity<String> setStatus(@RequestParam long id, @RequestParam DiscountStatus status){
        return ResponseEntity.ok(discountService.setDiscountStatus(id, status));
    }

}
