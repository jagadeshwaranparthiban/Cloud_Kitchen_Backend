package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.exception.UserNotFoundException;
import com.cloudkitchenbackend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private UserRepo userDetailsRepo;

    @Autowired
    public CustomUserDetailService(UserRepo userDetailsRepo){
        this.userDetailsRepo=userDetailsRepo;
    }

    public CustomUserDetailService(){}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRepo.findByUserName(username).get();
    }
}
