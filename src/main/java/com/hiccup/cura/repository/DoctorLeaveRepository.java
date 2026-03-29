package com.hiccup.cura.repository;

import com.hiccup.cura.model.DoctorLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, Long> {
    @Query("Select COUNT(l)>0 FROM DoctorLeave l WHERE l.doctorProfile.id=:doctorId "+
        "AND l.startDate <= :endDate AND l.endDate>= :startDate")
    boolean existsOverlappingLeave(@Param("doctorId") Long doctorId,@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(l) > 0 FROM DoctorLeave l WHERE l.doctorProfile.id = :doctorId " +
            "AND l.id != :leaveId " +
            "AND l.startDate <= :endDate AND l.endDate >= :startDate")
    boolean existsOverlappingLeaveExcludingCurrent(
            @Param("doctorId") Long doctorId,
            @Param("leaveId") Long leaveId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<DoctorLeave> findByDoctorProfile_id(Long doctorProfileId);

    @Query("SELECT COUNT(l)>0 FROM DoctorLeave l WHERE l.doctorProfile.id=:doctorId"+
        " AND l.startDate<=:date AND l.endDate>=:startDate")
    boolean isOnLeave(@Param("doctorId")Long doctorId,@Param("date") LocalDate date);
}
