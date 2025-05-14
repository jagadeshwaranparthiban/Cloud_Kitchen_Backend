package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.controller.HomeController;
import com.cloudkitchenbackend.exception.ItemAlreadyExistsException;
import com.cloudkitchenbackend.exception.ItemNotFoundException;
import com.cloudkitchenbackend.model.Item;
import com.cloudkitchenbackend.repository.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuService {

    private ItemRepo itemRepo;

    @Autowired
    public MenuService(ItemRepo itemRepo){
        this.itemRepo=itemRepo;
    }

    String[] menu={"idly","dosa","briyani"};

    public List<Item> getItems(){
        return itemRepo.findAll();
    }

    public String getItem(String name) {
        for(String item: menu){
            if(name.equals(item)) return "Item found: "+name;
        }
        return "Item not found";
    }

    public void addItem(Item item){
        if(itemRepo.existsById(item.getItemId())){
            throw new ItemAlreadyExistsException("Item "+item.getItemName()+" already exists");
        }else{
            itemRepo.save(item);
        }
    }

    public void deleteItem(String name) {
        List<Item> items=itemRepo.findAll();
        for(Item item: items){
            if(item.getItemName().equals(name)){
                itemRepo.deleteById(item.getItemId());
                return;
            }
        }
        throw new ItemNotFoundException("Item not found");
    }
}
