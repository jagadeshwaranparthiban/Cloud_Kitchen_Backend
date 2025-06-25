package com.cloudkitchenbackend.repository;

import com.cloudkitchenbackend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, String> {
    Optional<Users> findByUserName(String userName);
    Optional<Users> findByEmail(String email);

    @Query("SELECT u.UserId from Users u WHERE u.userName = :userName")
    String findUserIdByUserName(@Param("userName") String userName);
}
