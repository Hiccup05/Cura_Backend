package com.hiccup.cura.security.jwt;

import com.hiccup.cura.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtConfig {
    @Value("${auth.token.jwt}")
    private String jwtSecret;

    public String getUserNameFromToken(String token){
        SecretKey key=key();

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }


    public String generateTokenFromUser(Authentication authentication){
        CustomUserDetails principal= (CustomUserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("authority", principal.getAuthorities())
                .signWith(key())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()*60*60*100))
                .compact();

    }

    public SecretKey key(){return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));}

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch(Exception e){
            throw new JwtException(e.getMessage());
        }
    }
}
