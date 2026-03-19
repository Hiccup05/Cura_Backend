package com.hiccup.cura.repository;

import com.hiccup.cura.model.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.time.DayOfWeek;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    boolean existsByDayOfWeek(DayOfWeek dayOfWeek);

    List<DoctorSchedule> findByDoctorProfile_id(Long doctorId);
}
