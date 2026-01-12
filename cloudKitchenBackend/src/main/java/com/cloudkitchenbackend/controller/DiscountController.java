package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.dto.DiscountInfoDto;
import com.cloudkitchenbackend.dto.OrderRequestDto;
import com.cloudkitchenbackend.dto.ValidDiscountsDto;
import com.cloudkitchenbackend.dto.ViewDiscountDto;
import com.cloudkitchenbackend.model.Discount;
import com.cloudkitchenbackend.model.DiscountStatus;
import com.cloudkitchenbackend.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/get")
    public ResponseEntity<ValidDiscountsDto> getValidDiscounts(@RequestParam double orderCost) {
        List<Discount> discount = discountService.getBestDiscount(orderCost);
        List<DiscountInfoDto> validDiscounts = discount.stream().map(d -> {
            return new DiscountInfoDto(d.getDiscountCode(),d.getDiscountType(), d.getDiscountValue());
        }).toList();
        return ResponseEntity.ok(new ValidDiscountsDto(validDiscounts));
    }
}
