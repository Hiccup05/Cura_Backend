package com.hiccup.cura.repository;

import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorAndAppointmentDateAndAppointmentTime(DoctorProfile doctor, LocalDate appointmentDate, LocalTime appointmentTime);


}
