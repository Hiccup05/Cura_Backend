package com.hiccup.cura.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime expiresAt;
    private boolean used;
    private LocalDateTime createdAt;
}
