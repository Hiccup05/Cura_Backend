package com.hiccup.cura.repository;

import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(RoleType roleType);
}
