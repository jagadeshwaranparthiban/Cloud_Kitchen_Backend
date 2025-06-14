package com.cloudkitchenbackend.controller;

import com.cloudkitchenbackend.model.Item;
import com.cloudkitchenbackend.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/item")
public class MenuController {

    private MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService){
        this.menuService=menuService;
    }

    @GetMapping("/menu")
    public ResponseEntity<List<Item>> getMenu(){
        return ResponseEntity.ok(menuService.getItems());
    }

    @GetMapping("/view")
    public ResponseEntity<Item> getItem(@RequestParam String name){
        return ResponseEntity.ok(menuService.getItem(name));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addItem(@RequestBody Item item){
        menuService.addItem(item);
        return ResponseEntity.ok("item added");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteItemByName(@RequestParam String name){
        menuService.deleteItem(name);
        return ResponseEntity.ok("Item removed from menu");
    }

    @PutMapping("/set/available")
    public ResponseEntity<String> setAvailable(@RequestParam String itemName){
        menuService.setToAvailable(itemName);
        return ResponseEntity.ok("Item: "+itemName+" set to available");
    }

    @PutMapping("/set/unavailable")
    public ResponseEntity<String> setUnavailable(@RequestParam String itemName){
        menuService.setToUnavailable(itemName);
        return ResponseEntity.ok("Item: "+itemName+" set to Unavailable");
    }
}
