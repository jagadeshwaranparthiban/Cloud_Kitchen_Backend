package com.cloudkitchenbackend.repository;

import com.cloudkitchenbackend.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {
    Optional<Item> findByItemName(String itemName);

    List<Item> findByItemNameIn(List<String> items);
}
