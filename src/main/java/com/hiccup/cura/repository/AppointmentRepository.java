package com.hiccup.cura.repository;

import com.hiccup.cura.dto.response.AppointmentResponseDto;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorAndAppointmentDateAndAppointmentTime(DoctorProfile doctor, LocalDate appointmentDate, LocalTime appointmentTime);

    int countByDoctorAndAppointmentDate(DoctorProfile doctor, LocalDate appointmentDate);

    @Query("SELECT A FROM Appointment A WHERE "+
    " A.id=:id AND ((A.patient Is NOT NULL AND A.patient.id=:userId) OR (A.receptionist IS NOT NULL AND A.receptionist.id=:userId))")
    Optional<Appointment> getAppointmentByIdAndUserId(@Param("id")Long id,@Param("userId") Long userId);
}
