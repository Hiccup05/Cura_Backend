package com.hiccup.cura.repository;

import com.hiccup.cura.model.Service;
import com.hiccup.cura.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findAllByIsActiveTrue();
    List<Service> findBySpecializationAndIsActiveTrue(Specialization specialization);
}
