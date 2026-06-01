package com.hiccup.cura.security;

import com.hiccup.cura.dto.reqeust.SignUpRequestDto;
import com.hiccup.cura.dto.response.LoginResponseDto;
import com.hiccup.cura.dto.response.SignupResponseDto;
import com.hiccup.cura.enums.AuthType;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.PatientProfile;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.PatientRepository;
import com.hiccup.cura.repository.RoleRepository;
import com.hiccup.cura.repository.UserRepository;
import com.hiccup.cura.service.EmailService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthUtil {
    private final PatientRepository patientRepository;
    @Value("${auth.token.jwt}")
    private String jwtSecret;

    private final UserRepository userRepository;
    private final RoleRepository repository;
    private final EmailService emailService;

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
                .expiration(new Date(System.currentTimeMillis()+86400000))
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

    public User createUser(SignUpRequestDto signRequestDto, AuthType authType, String providerId) {
        User user=userRepository.findByUsername(signRequestDto.getUsername()).orElse(null);
        if(user!=null) throw new IllegalArgumentException("User already exists");

        user= new User(signRequestDto.getUsername(), signRequestDto.getPassword(), authType, providerId);
        user.setRole(Set.of(repository.findByName(RoleType.PATIENT)));
        user.setActive(true);
        user.setUsername(signRequestDto.getName());
        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(user);
        patientRepository.save(patientProfile);
        return userRepository.save(user);
    }

    public SignupResponseDto signUp(SignUpRequestDto signRequestDto) {
        User user= createUser(signRequestDto, null, null);
        return new SignupResponseDto(user.getId(), user.getEmail());
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> handleOauth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        // providerType and ProviderId
        // save the provider type and provider id info with user
        // if the user has an account: directly login
        // if not sign in and then login

        AuthType authType=getAuthProviderTypeFromRegistration(registrationId);
        String providerId=determineProviderIdFromOAuth2User(oAuth2User, registrationId);

        User user=userRepository.findByProviderIdAndAuthType(providerId, authType).orElse(null);
        String email=oAuth2User.getAttribute("email");
        //need to use user
        String name=oAuth2User.getAttribute("name");

        User emailUser=userRepository.findByEmail(email).orElse(null);

        if(user==null && emailUser==null){
            String username=determineUserFromOAuth2User(oAuth2User,registrationId, providerId);
            user = createUser(SignUpRequestDto.builder().username(username).password(null).name(name).build(), authType, providerId);

            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
            } catch (Exception e) {
                log.warn("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage());
            }

        }else if(user!=null){
            if(email!=null && !email.isBlank() && !email.equals(user.getUsername())){
                user.setUsername(email);
                userRepository.save(user);
            }
        } else{
            throw new BadCredentialsException("This email is already registered with provider "+ emailUser.getProviderId());
        }
        return ResponseEntity.ok(new LoginResponseDto(generateTokenFromUser(new CustomUser(user)), user.getId()));
    }
}
