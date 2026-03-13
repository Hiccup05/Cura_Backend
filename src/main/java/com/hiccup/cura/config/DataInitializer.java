package com.hiccup.cura.config;

import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.Role;
import com.hiccup.cura.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (RoleType roleType : RoleType.values()) {
            if (roleRepository.findByName(roleType)==null) {
                roleRepository.save(new Role(roleType));
            }
        }
    }
}
