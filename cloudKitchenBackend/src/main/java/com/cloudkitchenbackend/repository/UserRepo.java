package com.cloudkitchenbackend.repository;

import com.cloudkitchenbackend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, String> {
    Optional<Users> findByUserName(String userName);
    Optional<Users> findByEmail(String email);
}
