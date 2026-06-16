package com.hiccup.cura.repository;

import com.hiccup.cura.model.ReactivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactivationTokenRepository extends JpaRepository<ReactivationToken, String> {
    List<ReactivationToken> findByEmailAndUsed(String email, boolean used);
}
