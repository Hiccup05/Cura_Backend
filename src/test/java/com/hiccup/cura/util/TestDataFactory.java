package com.hiccup.cura.util;

import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.Role;
import com.hiccup.cura.model.User;
import net.datafaker.Faker;
import java.util.Set;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    /**
     * Generates a realistic User profile with a PATIENT role.
     */
    public static User createPatient() {
        User user = new User();
        user.setId(faker.number().randomNumber());
        user.setEmail(faker.internet().emailAddress());
        user.setActive(true);
        // Setup the role exactly how your security model expects it
        Role role = new Role();
        role.setName(RoleType.PATIENT);
        user.setRole(Set.of(role));

        return user;
    }

    /**
     * Generates a Doctor profile for testing authorization boundaries.
     */
    public static User createDoctor() {
        User doctor = createPatient(); // Reuse the basic profile generation

        Role role = new Role();
        role.setName(RoleType.DOCTOR);
        doctor.setRole(Set.of(role));

        return doctor;
    }
}
