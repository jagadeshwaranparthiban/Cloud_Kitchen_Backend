package com.cloudkitchenbackend.filter;

import com.cloudkitchenbackend.exception.InvalidTokenException;
import com.cloudkitchenbackend.service.CustomUserDetailService;
import com.cloudkitchenbackend.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private JWTUtil jwtUtil;
    private CustomUserDetailService userDetailService;

    @Autowired
    public JwtAuthFilter(JWTUtil jwtUtil, CustomUserDetailService userDetailService){
        this.jwtUtil=jwtUtil;
        this.userDetailService=userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader=request.getHeader("authorization"); //get the authorization header from request
        String token=null;
        String username=null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token=authHeader.substring(7); //extract the token part
            username=jwtUtil.extractUsername(token);
            System.out.println(username);
        }

        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails=userDetailService.loadUserByUsername(username);
            if(jwtUtil.validateToken(token,username,userDetails)){
                UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }else{
                throw new InvalidTokenException("Invalid or expired jwt!");
            }
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/login")
                || path.equals("/register")
                || path.equals("/refresh")
                || path.equals("/logout");
    }
}
