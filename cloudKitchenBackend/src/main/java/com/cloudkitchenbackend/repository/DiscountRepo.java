package com.cloudkitchenbackend.repository;

import com.cloudkitchenbackend.model.Discount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepo extends JpaRepository<Discount, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("SELECT d FROM Discount d WHERE d.discountCode = :code")
    Discount findDiscountByCode(String code);

    Optional<Discount> findTopByOrderByMinLevelAsc();

    @Transactional
    @Query("SELECT d FROM Discount d WHERE :orderCost >= d.minLevel ORDER BY d.minLevel DESC LIMIT 3")
    Optional<List<Discount>> findBestEligibleDiscount(@Param("orderCost") double orderCost);

    Optional<Discount> findByDiscountCode(String discountCode);
}
