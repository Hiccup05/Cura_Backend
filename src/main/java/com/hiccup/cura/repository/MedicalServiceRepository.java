package com.hiccup.cura.repository;

import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.model.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {
    @Query("SELECT new com.hiccup.cura.dto.response.MedicalServiceResponseDTO(" +
            "s.id, s.name, s.price, s.durationMinutes, s.description, s.isActive, " +
            "sp.id, sp.name) " +
            "FROM MedicalService s JOIN s.specialization sp " +
            "WHERE s.isActive = true")
    List<MedicalService> findAllByIsActiveTrue();
    @Query("SELECT new com.hiccup.cura.dto.response.MedicalServiceResponseDTO(" +
            "s.id, s.name, s.price, s.durationMinutes, s.description, s.isActive, " +
            "sp.id, sp.name) " +
            "FROM MedicalService s JOIN s.specialization sp " +
            "WHERE s.specialization.id = :specializationId AND s.isActive = true")
    List<MedicalServiceResponseDto> findAllActiveServicesWithSpecialization(@Param("specializationId") Long id);
}
