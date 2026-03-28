package com.hiccup.cura.model;

import com.hiccup.cura.enums.ReceptionistStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceptionistProfile {
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private ReceptionistStatus status;

    @MapsId
    @OneToOne
    private User user;
}
