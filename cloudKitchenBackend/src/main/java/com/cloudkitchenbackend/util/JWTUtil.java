package com.cloudkitchenbackend.util;

import com.cloudkitchenbackend.model.Role;
import com.cloudkitchenbackend.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;

import static java.lang.reflect.Array.get;

@Component
public class JWTUtil {

    @Value("${spring.jwt.secret}")
    private String secret;
    private SecretKey key;
    private final long expiration=1000*60*10; //10 minutes

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }


    public String generateToken(String userName){
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser().
                setSigningKey(key).
                build()
                .parseSignedClaims(token)
                .getBody();
    }

    public String extractUsername(String token){
        return getClaims(token).getSubject();
    }
    public Date extractExpiration(String token){
        return getClaims(token).getExpiration();
    }
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public boolean validateToken(String token, String username, UserDetails userDetails){
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
