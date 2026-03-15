package com.hiccup.cura.repository;

import com.hiccup.cura.enums.Specializations;
import com.hiccup.cura.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    boolean existsByName(Specializations name);
}
