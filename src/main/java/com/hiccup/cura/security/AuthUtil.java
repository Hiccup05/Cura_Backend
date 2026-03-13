package com.hiccup.cura.security;

import com.hiccup.cura.enums.AuthType;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthUtil {
    @Value("${auth.token.jwt}")
    private String jwtSecret;

    public String getUserNameFromToken(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public String generateTokenFromUser(CustomUser user){
        return Jwts.builder()
                .claim("authority", user.getAuthorities())
                .subject(user.getUsername())
                .signWith(getKey())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+60*60*100))
                .compact();
    }

    public SecretKey getKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch(Exception e){
            throw new JwtException(e.getMessage());
        }
    }

    public AuthType getAuthProviderTypeFromRegistration(String registrationId){
        return switch(registrationId.toLowerCase()){
            case "google"-> AuthType.GOOGLE;
            case "facebook" -> AuthType.FACEBOOK;
            default -> throw new IllegalArgumentException("Unsupported Oauth2 provider: "+registrationId);
        };
    }

    public String determineProviderIdFromOAuth2User(OAuth2User oAuth2User, String registrationId){
        String providerId=switch (registrationId.toLowerCase()){
            case "google"->oAuth2User.getAttribute("sub");
            case "facebook"-> oAuth2User.getAttribute("id");
            default -> throw new IllegalArgumentException("Unsupported Oauth2 provider: "+registrationId);
        };

        if(providerId==null || providerId.isBlank()){
            log.error("Unable to determine providerId for provider: {}", registrationId);
            throw new IllegalArgumentException("Unable to determine providerId for OAuth2 login");
        }

        return providerId;
    }

    public String determineUserFromOAuth2User(OAuth2User oAuth2user, String registrationId, String providerId){
        String email=oAuth2user.getAttribute("email");
        if(email!=null && !email.isBlank()){
            return email;
        }
        return switch (registrationId.toLowerCase()){
            case "google" ->oAuth2user.getAttribute("sub");
            case "facebook" -> oAuth2user.getAttribute("name");
            default -> providerId;
        };
    }
}
