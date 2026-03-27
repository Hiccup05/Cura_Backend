package com.hiccup.cura.repository;

import com.hiccup.cura.dto.reqeust.LeaveRequestDto;
import com.hiccup.cura.model.DoctorLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, Long> {
    @Query("Select COUNT(l)>0 FROM DoctorLeave l WHERE l.doctorProfile.id=:doctorId "+
        "AND l.startDate <= :endDate AND l.endDate>= :startDate")
    boolean existsOverlappingLeave(@Param("doctorId") Long doctorId,@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);
}
