package com.cloudkitchenbackend.util;

import com.cloudkitchenbackend.model.Role;
import com.cloudkitchenbackend.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {
    private final String secret="This-is-A-Sample-SeCrEt-07142128@*";
    private final SecretKey key= Keys.hmacShaKeyFor(secret.getBytes());
    private final long expiration=1000*60*10; //10 minutes
    
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
