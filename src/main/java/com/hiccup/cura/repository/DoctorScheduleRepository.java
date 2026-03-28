package com.hiccup.cura.repository;

import com.hiccup.cura.model.DoctorProfile;
import com.hiccup.cura.model.DoctorSchedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.time.DayOfWeek;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    boolean existsByDayOfWeekAndDoctorProfile_Id(DayOfWeek dayOfWeek, Long doctorId);

    @EntityGraph(attributePaths = {"doctorProfile", "doctorProfile.user"})
    List<DoctorSchedule> findByDoctorProfile_id(Long doctorId);

    Optional<DoctorSchedule> findByIdAndDoctorProfile(Long id, DoctorProfile doctorProfile);
}
