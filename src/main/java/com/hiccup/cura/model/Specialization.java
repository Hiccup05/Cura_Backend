package com.hiccup.cura.model;

<<<<<<<< HEAD:src/main/java/com/hiccup/cura/model/AdminProfile.java
import com.hiccup.cura.enums.AdminType;
import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.enums.Role;
========
>>>>>>>> project2:src/main/java/com/hiccup/cura/model/Specialization.java
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
<<<<<<<< HEAD:src/main/java/com/hiccup/cura/model/AdminProfile.java
@Entity
@Table(name="admin_profile")
public class AdminProfile {
    private Long id;
    @JoinColumn(name="user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private AdminType role;
========
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer slotDuration;
>>>>>>>> project2:src/main/java/com/hiccup/cura/model/Specialization.java
}
