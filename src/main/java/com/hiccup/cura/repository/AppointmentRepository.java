package com.hiccup.cura.repository;

import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.model.DoctorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusNot(
            DoctorProfile doctor,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            AppointmentStatus status
    );

    int countByDoctorAndAppointmentDate(DoctorProfile doctor, LocalDate appointmentDate);

    @Query("SELECT A FROM Appointment A WHERE "+
    " A.id=:id AND ((A.patient Is NOT NULL AND A.patient.id=:userId) OR (A.receptionist IS NOT NULL AND A.receptionist.id=:userId))")
    Optional<Appointment> getAppointmentByIdAndUserId(@Param("id")Long id,@Param("userId") Long userId);
    @Query("Select A From Appointment A Where " +
    "((A.patient Is NOT NULL AND A.patient.id=:userId) OR (A.receptionist IS NOT NULL AND A.receptionist.id=:userId))" +
    " ORDER BY A.appointmentDate desc")
    Page<Appointment> getAppointmentOfUser(@Param("userId") Long userId, Pageable pageable);

    List<Appointment> findByStatus(AppointmentStatus appointmentStatus);

    @Query(value = """
    select *
    from appointment a
    where a.type = 'RECEPTIONIST_BOOKED'
      and (:receptionistId is null or a.receptionist_id = :receptionistId)
      and (:walkInPatientName is null or a.walk_in_patient_name ilike concat('%', :walkInPatientName, '%'))
    order by a.booked_at desc
""", nativeQuery = true)
    List<Appointment> findReceptionistBookedAppointments(
            @Param("receptionistId") Long receptionistId,
            @Param("walkInPatientName") String walkInPatientName
    );
}
