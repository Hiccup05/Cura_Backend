package com.hiccup.cura.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactivationToken {
    @Id
    private String token;
    private String email;
    private Instant expiresAt;
    private boolean used;
    private Instant createdAt;
}
