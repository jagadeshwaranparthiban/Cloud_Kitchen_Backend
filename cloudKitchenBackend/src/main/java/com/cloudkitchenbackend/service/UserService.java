package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.dto.NewUserDto;
import com.cloudkitchenbackend.dto.SuccessfulResponse;
import com.cloudkitchenbackend.dto.UserLoginDto;
import com.cloudkitchenbackend.exception.UserAlreadyExistsException;
import com.cloudkitchenbackend.exception.UserNotFoundException;
import com.cloudkitchenbackend.model.Role;
import com.cloudkitchenbackend.model.Users;
import com.cloudkitchenbackend.repository.UserRepo;
import com.cloudkitchenbackend.util.JWTUtil;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private UserRepo userRepo;
    private JWTUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, JWTUtil jwtUtil, AuthenticationManager authenticationManager,
    PasswordEncoder passwordEncoder){
        this.userRepo=userRepo;
        this.jwtUtil=jwtUtil;
        this.authenticationManager=authenticationManager;
        this.passwordEncoder=passwordEncoder;
    }

    public String authUser(UserLoginDto loginCred){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginCred.getUserName(), loginCred.getPassword()));
            String token=jwtUtil.generateToken(loginCred.getUserName());
            return token;
        }catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
//        Optional<Users> userName=userRepo.findByUserName(loginCred.getUserName());
//        if(userName.isEmpty()) throw new UserNotFoundException("User not found. Sign up!");
//        return "Logged in as: "+userName.get().getUserName()+", role: "+userName.get().getRole();
    }

    public String generateUserId(){
        return UUID.randomUUID().toString().replace("-","").substring(1,12);
    }

    public SuccessfulResponse RegisterUser(NewUserDto newUserData){
        Optional<Users> user=userRepo.findByEmail(newUserData.getEmailId());
        Optional<Users> currUser=userRepo.findByUserName(newUserData.getUserName());
        if(user.isPresent()){
            throw new UserAlreadyExistsException("User with emailID: "+user.get().getEmail()+" already exists.");
        }
        if(currUser.isPresent()){
            throw new UserAlreadyExistsException("Username: "+newUserData.getUserName()+" already exists.");
        }

        String newUserId=generateUserId();

        userRepo.save(new Users(newUserId,
                newUserData.getUserName(),
                newUserData.getEmailId(),
                passwordEncoder.encode(newUserData.getPassword()),
                newUserData.getRole()
        ));
        return new SuccessfulResponse("User "+newUserData.getUserName()+" registered successfylly! Login now!");
    }
}
