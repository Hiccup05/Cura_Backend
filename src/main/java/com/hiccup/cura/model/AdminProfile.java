package com.hiccup.cura.model;

import com.hiccup.cura.enums.AdminType;
import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="admin_profile")
public class AdminProfile {
    private Long id;
    @JoinColumn(name="user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private AdminType role;
}
