package com.hiccup.cura.repository;

import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByAppointment_IdAndPaymentStatusNotIn(Long appointmentId, List<PaymentStatus> paymentStatus);

    Payment findByPidx(String pidx);

    @Query("SELECT COALESCE(SUM(p.amount)) FROM Payment p where p.paymentStatus= :paymentStatus")
    BigDecimal sumAmountByPaymentStatus(@Param("paymentStatus") PaymentStatus paymentStatus);
}
