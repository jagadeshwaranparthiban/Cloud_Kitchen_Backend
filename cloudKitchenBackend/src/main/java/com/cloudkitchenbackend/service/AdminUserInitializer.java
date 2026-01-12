package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.model.Role;
import com.cloudkitchenbackend.model.Users;
import com.cloudkitchenbackend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Optional;


@Component
public class AdminUserInitializer {
    @Autowired
    private UserService userService;

    @Bean
    public CommandLineRunner createAdminUser(UserRepo userRepo, PasswordEncoder encoder){
        return args->{
            Optional<Users> admin=userRepo.findByUserName("admin");
            if(admin.isEmpty()){
                Users user=new Users();
                user.setUserId(userService.generateUserId());
                user.setUserName("admin");
                user.setEmail("admin123@gmail.com");
                user.setPassword(encoder.encode("Admin123"));
                user.setRole(Role.ROLE_ADMIN);

                userRepo.save(user);
                System.out.println("default admin user created");
            }
        };
    }
}
