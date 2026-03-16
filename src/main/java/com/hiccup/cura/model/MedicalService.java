package com.hiccup.cura.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class MedicalService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private Integer durationMinutes;
    private String description;
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;
}
