package com.hiccup.cura.model;

import com.hiccup.cura.enums.Role;
import com.hiccup.cura.enums.StaffRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "staff_profile")
public class StaffProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private StaffRole staffRole;
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;
}
