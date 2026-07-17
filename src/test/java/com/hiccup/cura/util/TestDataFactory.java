package com.hiccup.cura.util;

import com.hiccup.cura.enums.*;
import com.hiccup.cura.model.*;
import com.hiccup.cura.repository.*;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Component
public class TestDataFactory {

    private static final Faker faker = new Faker();
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientProfileRepository;

    @Autowired
    private MedicalServiceRepository medicalServiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Generates a realistic User profile with a PATIENT role.
     */
    public static User createPatient() {
        User user = new User();
        user.setId(1L);
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

    public static DoctorProfile createDoctorProfile(){
       DoctorProfile doctorProfile=new DoctorProfile();
       doctorProfile.setFirstName("");
        doctorProfile.setLastName(" ");
        doctorProfile.setDoctorStatus(DoctorStatus.ACTIVE);
        doctorProfile.setUser(createDoctor());
        doctorProfile.setId(1L);
        doctorProfile.setSpecialization(Set.of(new Specialization(1L, "Cardiologist", 60)));
        return doctorProfile;
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

    @Transactional
    public Appointment createPendingAppointmentForNewPatient(BigDecimal servicePrice) {
        Role patientRole = roleRepository.save(Role.builder().name(RoleType.PATIENT).build());

        User user = userRepository.save(User.builder()
                .email("patient-" + UUID.randomUUID() + "@test.com")
                .active(true)
                .role(Set.of(patientRole))
                .build());

        PatientProfile patientProfile = patientProfileRepository.save(
                PatientProfile.builder()
                        .user(user)
                        .firstName("Test")
                        .lastName("Patient")
                        .build());

        MedicalService medicalService = medicalServiceRepository.save(
                MedicalService.builder()
                        .name("Test Service")
                        .price(servicePrice)
                        .isActive(true)
                        .build());

        return appointmentRepository.save(Appointment.builder()
                .patient(patientProfile)
                .medicalService(medicalService)
                .status(AppointmentStatus.PENDING)
                .build());
    }

    @Transactional
    public void createPendingPayment(BigDecimal servicePrice) {
        Role patientRole = roleRepository.save(Role.builder().name(RoleType.PATIENT).build());

        User user = userRepository.save(User.builder()
                .email("patient-" + UUID.randomUUID() + "@test.com")
                .active(true)
                .role(Set.of(patientRole))
                .build());

        PatientProfile patientProfile = patientProfileRepository.save(
                PatientProfile.builder()
                        .user(user)
                        .firstName("Test")
                        .lastName("Patient")
                        .build());

        MedicalService medicalService = medicalServiceRepository.save(
                MedicalService.builder()
                        .name("Test Service")
                        .price(servicePrice)
                        .isActive(true)
                        .build());

        Appointment pendingAppointmentForNewPatient  = appointmentRepository.save(Appointment.builder()
                .patient(patientProfile)
                .medicalService(medicalService)
                .status(AppointmentStatus.PENDING)
                .build());

        Payment payment=new Payment();
        payment.setPaymentType(PaymentType.ESEWA);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(pendingAppointmentForNewPatient.getMedicalService().getPrice());
        payment.setAppointment(pendingAppointmentForNewPatient);
        payment.setPidx(String.valueOf(1));
        payment.setPaymentUrl("https://rc-epay.esewa.com.np/api/epay/main/v2/form");
        payment.setExpiresAt(OffsetDateTime.now().plusMinutes(15));
        paymentRepository.save(payment);
    }
}
