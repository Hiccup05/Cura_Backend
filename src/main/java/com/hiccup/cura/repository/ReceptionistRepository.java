package com.hiccup.cura.repository;

import com.hiccup.cura.model.ReceptionistProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceptionistRepository extends JpaRepository<ReceptionistProfile, Long> {
}
