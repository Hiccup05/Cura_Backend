package com.hiccup.cura.repository;

import com.hiccup.cura.model.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<PatientProfile, Long> {

}
