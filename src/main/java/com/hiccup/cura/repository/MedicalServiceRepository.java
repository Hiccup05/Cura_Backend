package com.hiccup.cura.repository;

import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.model.MedicalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {
    @EntityGraph(attributePaths = {"specialization"})
    List<MedicalService> findAll();

    @EntityGraph(attributePaths = {"specialization"})
    Optional<MedicalService> findById(Long id);

    @EntityGraph(attributePaths = {"specialization"})
    Page<MedicalService> findAllByIsActiveTrue(Pageable pageable);
    @Query("SELECT new com.hiccup.cura.dto.response.MedicalServiceResponseDto(" +
            "s.id, s.name, s.price, s.durationMinutes, s.description, s.isActive, " +
            "sp.id, sp.name, s.photoUrl) " +
            "FROM MedicalService s JOIN s.specialization sp " +
            "WHERE s.specialization.id = :specializationId AND s.isActive = true")
    List<MedicalServiceResponseDto> findAllActiveServicesWithSpecialization(@Param("specializationId") Long specializationId);

    @Query("SELECT m FROM MedicalService m where m.id=:id AND m.isActive=true")
    Optional<MedicalService> findActiveMedicalServiceById(@Param("id") Long id);

    boolean existsByNameAndSpecialization_id(String name, Long specializationId);

    @Query("select m from MedicalService m where LOWER(m.name) like lower(Concat('%', :keyword , '%')) ")
    Page<MedicalService> searchByKeyword(@RequestParam("keyword") String keyword, Pageable pageable);
}
