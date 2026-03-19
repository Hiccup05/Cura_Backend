package com.hiccup.cura.config;

import com.hiccup.cura.enums.AuthType;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.Role;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.RoleRepository;
import com.hiccup.cura.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.init.email1}")
    private String adminEmail1;
    @Value("${admin.init.password1}")
    private String adminPassword1;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        initRoles();
        initAdmins();
    }

    private void initRoles() {
        for (RoleType roleType : RoleType.values()) {
            if (roleRepository.findByName(roleType) == null) {
                roleRepository.save(new Role(roleType));
            }
        }
    }

    private void initAdmins() {
        createAdminIfNotExists(adminEmail1, adminPassword1);
        // repeat for admin 2 and 3
    }

    private void createAdminIfNotExists(String email, String password) {
        if (userRepository.existsByEmail(email)) return;
        User user=new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Set.of(roleRepository.findByName(RoleType.ADMIN), roleRepository.findByName(RoleType.PATIENT)));
        user.setAuthType(AuthType.LOCAL);
        userRepository.save(user);
    }
}
