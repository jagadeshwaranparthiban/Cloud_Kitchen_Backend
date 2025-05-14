package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.model.Item;
import com.cloudkitchenbackend.service.HomeService;
import com.cloudkitchenbackend.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class HomeController {

    private MenuService menuService;
    private HomeService homeService;

    @Autowired
    public HomeController(MenuService menuService, HomeService homeService){
        this.menuService=menuService;
        this.homeService=homeService;
    }

    @GetMapping("/")
    public String home(){
        return "Hello world";
    }

    @GetMapping("/menu")
    public ResponseEntity<List<Item>> getMenu(){
        return ResponseEntity.ok(menuService.getItems());
    }

    @GetMapping("/item")
    public String getItem(@RequestParam String name){
        return menuService.getItem(name);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addItem(@RequestBody Item item){
        System.out.println(item.getDescription());
        menuService.addItem(item);
        return ResponseEntity.ok("item added");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteItemByName(@RequestParam String name){
        menuService.deleteItem(name);
        return ResponseEntity.ok("Item removed from menu");
    }
}
