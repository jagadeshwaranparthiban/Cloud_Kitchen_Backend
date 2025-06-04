package com.cloudkitchenbackend.repository;

import com.cloudkitchenbackend.model.Discount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepo extends JpaRepository<Discount, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("SELECT d FROM Discount d WHERE d.discountCode = :code")
    Discount findDiscountByCode(String code);
}
