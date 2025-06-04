package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.dto.ViewDiscountDto;
import com.cloudkitchenbackend.exception.DiscountCodeNotFoundException;
import com.cloudkitchenbackend.model.Discount;
import com.cloudkitchenbackend.model.DiscountStatus;
import com.cloudkitchenbackend.repository.DiscountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DiscountService {

    private DiscountRepo discountRepo;

    @Autowired
    public DiscountService(DiscountRepo discountRepo){
        this.discountRepo=discountRepo;
    }

    public String addDiscountCode(Discount discount){
        discountRepo.save(discount);
        return "Discount code added to database";
    }

    public String removeDiscountCode(String code){
        Discount discount=discountRepo.findDiscountByCode(code);
        if(discount==null) return "Discount code not found";

        discountRepo.delete(discount);
        return "Discount removed from database.";
    }

    public ViewDiscountDto viewDiscount(long id){
        Optional<Discount> discount=discountRepo.findById(id);
        if(discount.isPresent()){
            ViewDiscountDto res=new ViewDiscountDto();
            res.setDiscountId(id);
            res.setDiscountCode(discount.get().getDiscountCode());
            res.setCurrentUsage(discount.get().getCurrentUsage());
            res.setMaxusage(discount.get().getMaxUsage());
            res.setStatus(discount.get().getStatus());
            return res;
        }
        throw new DiscountCodeNotFoundException("Discount code not found.");
    }

    public String setDiscountStatus(long id, DiscountStatus status){
        Optional<Discount> d=discountRepo.findById(id);
        if(d.isEmpty()) throw new DiscountCodeNotFoundException("Discount code not found.");

        Discount discount=d.get();
        if(discount.getStatus()==status) return "Discount already in status: "+status;
        discount.setStatus(status);
        discountRepo.save(discount);

        return "discount: "+discount.getDiscountCode()+" set to status: "+status;
    }
}
