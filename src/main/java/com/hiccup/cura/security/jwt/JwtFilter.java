package com.hiccup.cura.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
     private final JwtConfig jwtConfig;
     private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String token=parseToken(request);
            if(token!=null && jwtConfig.validateToken(token)){
                String userNameFromToken=jwtConfig.getUserNameFromToken(token);
                UserDetails userDetails=userDetailsService.loadUserByUsername(userNameFromToken);
                Authentication authentication=new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        }
        catch(UsernameNotFoundException | JwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid or expiredToken\"}");
        }
    }

    private String parseToken(HttpServletRequest request) {
        String authorization=request.getHeader("Authorization");
        if(authorization==null || authorization.isBlank() || !authorization.contains("Bearer")){
            return null;
        }
        return authorization.substring(7);
    }
}
