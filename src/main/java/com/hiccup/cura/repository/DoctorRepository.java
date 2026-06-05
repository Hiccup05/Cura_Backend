package com.hiccup.cura.repository;

import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.model.DoctorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<DoctorProfile> getPublicDoctors(@Param("status") List<DoctorStatus> status, Pageable pageable);

    @Query("SELECT d FROM DoctorProfile d where d.id=:id AND d.doctorStatus IN :status")
    Optional<DoctorProfile> getPublicDoctor(@Param("id") Long id, @Param("status") List<DoctorStatus> status);

    long countByDoctorStatusNot(DoctorStatus doctorStatus);

    @Query("SELECT d FROM DoctorProfile d WHERE LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<DoctorProfile> searchByName(@Param("name") String name, Pageable pageable);
}
