package com.hiccup.cura.repository;

import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorProfile, Long> {

    boolean existsById(Long id);

    @Query("SELECT d FROM DoctorProfile d where d.doctorStatus IN :status")
    List<DoctorProfile> getPublicDoctors(@Param("status") List<DoctorStatus> status);

    @Query("SELECT d FROM DoctorProfile d where d.id=:id AND d.doctorStatus IN :status")
    Optional<DoctorProfile> getPublicDoctor(@Param("id") Long id, @Param("status") List<DoctorStatus> status);

    long countByDoctorStatusNot(DoctorStatus doctorStatus);
}
