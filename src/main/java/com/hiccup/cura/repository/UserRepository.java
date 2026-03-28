package com.hiccup.cura.repository;

import com.hiccup.cura.enums.AuthType;
import com.hiccup.cura.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderIdAndAuthType(String providerId, AuthType authType);

    boolean existsByEmail(String email);
}
