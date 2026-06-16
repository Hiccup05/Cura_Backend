package com.hiccup.cura.service;

import com.hiccup.cura.dto.reqeust.ReactivationTokenRequestDto;
import com.hiccup.cura.exception.custom.InvalidReactivationTokenException;
import com.hiccup.cura.exception.custom.ReactivationTokenExpiredException;
import com.hiccup.cura.model.ReactivationToken;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.ReactivationTokenRepository;
import com.hiccup.cura.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReactivationTokenService {
    @Value("${frontend.url}")
    private String frontendUrl;
    private final ReactivationTokenRepository reactivationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public String registerToken(String email){
        User user = userRepository.findByEmail(email).orElse(null);
        if(user==null || user.isActive()){
            return "if your account exists you will receive an email";
        }
        List<ReactivationToken> byEmailAndUsed = reactivationTokenRepository.findByEmailAndUsed(email, false);
        if(!byEmailAndUsed.isEmpty()) reactivationTokenRepository.delete(byEmailAndUsed.getFirst());
        ReactivationToken token = createToken(email);
        reactivationTokenRepository.save(token);
        String reactivationUrl=frontendUrl+"/reactivate?token=" + token.getToken();
        emailService.sendReactivationTokenUrl(email, reactivationUrl);
        return "if your account exists you will receive an email";
    }

    @Transactional
    public void reactivate(ReactivationTokenRequestDto tokenRequestDto){
        User user = userRepository.findByEmail(tokenRequestDto.getEmail()).orElse(null);
        if(user==null || user.isActive()){
            return;
        }
        ReactivationToken token=reactivationTokenRepository.findById(tokenRequestDto.getToken()).orElseThrow(()->new InvalidReactivationTokenException("Token not found: " + tokenRequestDto.getToken()));
        if(token.isUsed()){
            throw new InvalidReactivationTokenException("Token already used: " + tokenRequestDto.getToken());
        }

        if(token.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new ReactivationTokenExpiredException("Token expired: " + tokenRequestDto.getToken());
        }
        if(!token.getEmail().equals(tokenRequestDto.getEmail())){
            throw new InvalidReactivationTokenException("Token mismatch: " + tokenRequestDto.getToken());
        }
        user.setActive(true);
        token.setUsed(true);
        reactivationTokenRepository.save(token);
        userRepository.save(user);
        emailService.sendReactivationSuccess(user.getEmail(), user.getUsername());
    }
    private ReactivationToken createToken(String email){
        return ReactivationToken.builder()
                .token(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .email(email)
                .used(false)
                .build();

    }
}
