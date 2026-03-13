package com.hiccup.cura.repository;

import com.hiccup.cura.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorProfile, Long> {

}
