package com.cloudkitchenbackend.service;

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

    public List<Item> getItems(){
        return itemRepo.findAll();
    }

    public Item getItem(String name) {
        Optional<Item> item=itemRepo.findByItemName(name);
        if(item.isEmpty()) throw new ItemNotFoundException("Item "+name+" not found");
        return item.get();
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

    public void setToAvailable(String itemName) {
        Optional<Item> item=itemRepo.findByItemName(itemName);
        if(item.isEmpty()) throw new ItemNotFoundException("Item not found");
        Item updatedItem=item.get();
        updatedItem.setAvailable(true);
        itemRepo.save(updatedItem);
    }

    public void setToUnavailable(String itemName){
        Optional<Item> item=itemRepo.findByItemName(itemName);
        if(item.isEmpty()) throw new ItemNotFoundException("Item not found");
        Item updatedItem=item.get();
        updatedItem.setAvailable(false);
        itemRepo.save(updatedItem);
    }
}
