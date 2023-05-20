package com.project.repository;

import com.project.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByOrderId(Long id);

    @Query("SELECT o FROM Payment o WHERE o.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findPaymentToday(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT SUM(p.amount) FROM Payment p")
    Long countByAmount();

    @Query("SELECT o FROM Payment o WHERE DATE(o.paymentDate) = DATE(:date)")
    List<Payment> findPaymentByDate(@Param("date") Date date);

    @Query("SELECT o FROM Payment o WHERE o.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findOPaymentByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT o FROM Payment o WHERE o.paymentDate BETWEEN :startOfWeek AND :endOfWeek")
    List<Payment> findPaymentForCurrentWeek(@Param("startOfWeek") Date startOfWeek, @Param("endOfWeek") Date endOfWeek);

    @Query("SELECT o FROM Payment o WHERE YEAR(o.paymentDate) = :year AND MONTH(o.paymentDate) = :month ")
    List<Payment> findPaymentForCurrentMonth(@Param("year") int year, @Param("month") int month);


    @Query("SELECT o FROM Payment o WHERE o.paymentDate = :yesterday")
    List<Payment> findPaymentFromYesterday(@Param("yesterday") Date yesterday);


    @Query("SELECT o FROM Payment o WHERE YEAR(o.paymentDate) = :year AND MONTH(o.paymentDate) = :month")
    List<Payment> findPaymentForPreviousMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT o FROM Payment o WHERE o.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findPaymentForLastWeek(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
