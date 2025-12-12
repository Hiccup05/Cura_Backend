package com.hiccup.cura.request;

import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.model.DoctorProfile;
import com.hiccup.cura.model.Product;
import com.hiccup.cura.model.StaffProfile;
import com.hiccup.cura.model.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class AppointmentDto {
    private Date createdAt;
    private AppointmentStatus status;
    private Date appointmentTime;
    @JoinColumn(name="created_by")
    @OneToMany
    private User createdBy;
    @OneToMany
    @JoinColumn(name="doctor_id")
    private DoctorProfile doctor;
    @OneToMany
    @JoinColumn(name="staff_id")
    private StaffProfile staffProfile;
    @OneToOne
    @JoinColumn(name="product_id")
    private Product product;
}
