package com.hiccup.cura.repository;

import com.hiccup.cura.model.MedicalService;
import com.hiccup.cura.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {
    List<MedicalService> findAllByIsActiveTrue();
    List<MedicalService> findAllBySpecializationAndIsActiveTrue(Specialization specialization);
}
