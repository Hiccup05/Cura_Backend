package com.hiccup.cura.util;

import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.enums.PaymentType;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.PatientProfile;
import com.hiccup.cura.model.Payment;
import com.hiccup.cura.model.Role;
import com.hiccup.cura.model.User;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
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

    public static PatientProfile createPatientProfile(){
        PatientProfile patientProfile=new PatientProfile();
        patientProfile.setUser(createPatient());
        patientProfile.setId(createPatient().getId());
        return patientProfile;
    }

    public static Payment createPayment(PaymentStatus paymentStatus){
        Payment payment=new Payment();
        payment.setPaymentStatus(paymentStatus);
        payment.setPaymentUrl("random url");
        payment.setPidx(String.valueOf(faker.number().randomNumber()));
        payment.setPaymentType(PaymentType.KHALTI);
        payment.setAmount(BigDecimal.ONE);
        payment.setPaidAt(LocalDateTime.now());
        payment.setExpiresAt(OffsetDateTime.from(LocalDateTime.now().plusHours(10)));
        return payment;
    }
}
