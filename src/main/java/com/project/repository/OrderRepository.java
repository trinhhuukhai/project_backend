package com.project.repository;

import com.project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long id);

    @Query("SELECT o FROM Order o WHERE o.status = ?1")
    List<Order> findByStatus(String status);

    @Query("SELECT o FROM Order o WHERE o.paymentStatus = ?1")
    List<Order> findByPaymentStatus(String paymentStatus);

    @Query("SELECT o FROM Order o WHERE DATE(o.orderDate) = DATE(:date)")
    List<Order> findOrderByDate(@Param("date") Date date);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startOfWeek AND :endOfWeek")
    List<Order> findOrdersForCurrentWeek(@Param("startOfWeek") Date startOfWeek, @Param("endOfWeek") Date endOfWeek);

    @Query("SELECT o FROM Order o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month ")
    List<Order> findOrdersForCurrentMonth(@Param("year") int year, @Param("month") int month);


    @Query("SELECT o FROM Order o WHERE o.orderDate > :yesterday")
    List<Order> findOrdersFromYesterday(@Param("yesterday") Date yesterday);


    @Query("SELECT o FROM Order o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month")
    List<Order> findOrdersForPreviousMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersForLastWeek(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
