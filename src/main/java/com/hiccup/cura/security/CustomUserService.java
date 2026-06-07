package com.hiccup.cura.security;

import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.exception.custom.PatientAccountDeactivatedException;
import com.hiccup.cura.exception.custom.StaffAccountDeactivatedException;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user= userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        if(!user.isActive()){
            boolean isPatient=user.getRole().stream().anyMatch(role-> role.getName().equals(RoleType.PATIENT));

            if(isPatient){
                throw new PatientAccountDeactivatedException("Your account is deactivated. Would you like to reactivate?");
            }
            throw new StaffAccountDeactivatedException("Your account has been deactivated. Please contact admin.");
        }
        return new CustomUser(user);
    }
}
