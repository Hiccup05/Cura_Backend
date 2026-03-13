package com.hiccup.cura.security;

import com.hiccup.cura.dto.response.LoginResponseDto;
import com.hiccup.cura.dto.reqeust.SignUpRequestDto;
import com.hiccup.cura.dto.response.SignupResponseDto;
import com.hiccup.cura.enums.AuthType;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.RoleRepository;
import com.hiccup.cura.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository repository;

    public User createUser(SignUpRequestDto signRequestDto, AuthType authType, String providerId) {
        User user=userRepository.findByUsername(signRequestDto.getUsername()).orElse(null);
        if(user!=null) throw new IllegalArgumentException("User already exists");

        user= new User(signRequestDto.getUsername(), signRequestDto.getPassword(), authType, providerId);
        user.setRole(Set.of(repository.findByName(RoleType.PATIENT)));
        user.setActive(true);
        user.setUsername(signRequestDto.getName());
        return userRepository.save(user);
    }

    public SignupResponseDto signUp(SignUpRequestDto signRequestDto) {
        User user= createUser(signRequestDto, null, null);
        return new SignupResponseDto(user.getId(), user.getEmail());
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> handleOauth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        // providerType and ProviderId
        // save the providertype and provider id info with user
        // if the user has an account: directly login
        // if not sign in and then login

        AuthType authType=authUtil.getAuthProviderTypeFromRegistration(registrationId);
        String providerId= authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);

        User user=userRepository.findByProviderIdAndAuthType(providerId, authType).orElse(null);
        String email=oAuth2User.getAttribute("email");
        //need to use user
        String name=oAuth2User.getAttribute("name");

        User emailUser=userRepository.findByEmail(email).orElse(null);

        if(user==null && emailUser==null){
            //signup flow
            String username= authUtil.determineUserFromOAuth2User(oAuth2User,registrationId, providerId);
            user = createUser(SignUpRequestDto.builder().username(username).password(null).name(name).build(), authType, providerId);

        }else if(user!=null){
            if(email!=null && !email.isBlank() && !email.equals(user.getUsername())){
                user.setUsername(email);
                userRepository.save(user);
            }
        } else{
            throw new BadCredentialsException("This email is already registered with provider "+ emailUser.getProviderId());
        }
        return ResponseEntity.ok(new LoginResponseDto(authUtil.generateTokenFromUser(new CustomUser(user)), user.getId()));
    }

}
