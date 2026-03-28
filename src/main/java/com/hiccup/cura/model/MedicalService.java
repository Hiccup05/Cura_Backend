package com.hiccup.cura.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private Integer durationMinutes;
    private String description;
    private Boolean isActive;
    private String preInstructions;

    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;
}
