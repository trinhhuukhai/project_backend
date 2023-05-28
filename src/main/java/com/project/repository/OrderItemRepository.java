package com.project.repository;

import com.project.model.OrderItem;
import com.project.model.Payment;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByShopIdOrderByOrderDateDesc(Long shopId);

    @Query("SELECT SUM(p.Total) FROM OrderItem p")
    Long countByTotal();

    List<OrderItem> findByShopIdAndPaymentStatus(Long shopId, String paymentStatus);

    @Query("SELECT DATE(o.orderDate) AS orderDate, SUM(o.Total) AS totalSales FROM OrderItem o WHERE o.shop.id = :shopId AND o.paymentStatus = :paymentStatus GROUP BY DATE(o.orderDate)")
    List<Object[]> getTotalSalesByDay(@Param("shopId") Long shopId, @Param("paymentStatus") String paymentStatus);

    @Query("SELECT o FROM OrderItem o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.shop.id = :shopId")
    List<OrderItem>  findOrderItemToday(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("shopId") Long shopId);


    @Query("SELECT o FROM OrderItem o WHERE DATE(o.orderDate) = DATE(:date) AND o.shop.id = :shopId")
    List<OrderItem> findOrderItemByDate(@Param("date") Date date, @Param("shopId") Long shopId);

    @Query("SELECT o FROM OrderItem o WHERE o.orderDate BETWEEN :startDate AND :endDate  AND o.shop.id = :shopId")
    List<OrderItem> findOOrderItemByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("shopId") Long shopId);

    @Query("SELECT o FROM OrderItem o WHERE o.orderDate BETWEEN :startOfWeek AND :endOfWeek AND o.shop.id = :shopId")
    List<OrderItem> findOrderItemForCurrentWeek(@Param("startOfWeek") Date startOfWeek, @Param("endOfWeek") Date endOfWeek, @Param("shopId") Long shopId );

    @Query("SELECT o FROM OrderItem o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month AND o.shop.id = :shopId")
    List<OrderItem> findOrderItemForCurrentMonth(@Param("year") int year, @Param("month") int month, @Param("shopId") Long shopId);


    @Query("SELECT o FROM OrderItem o WHERE DATE(o.orderDate) = DATE(:yesterday) AND o.shop.id = :shopId")
    List<OrderItem> findOrderItemFromYesterday(@Param("yesterday") Date yesterday, @Param("shopId") Long shopId);



    @Query("SELECT o FROM OrderItem o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month AND o.shop.id = :shopId")
    List<OrderItem> findOrderItemForPreviousMonth(@Param("year") int year, @Param("month") int month,  @Param("shopId") Long shopId);

    @Query("SELECT o FROM OrderItem o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.shop.id = :shopId")
    List<OrderItem> findOrderItemForLastWeek(@Param("startDate") Date startDate, @Param("endDate") Date endDate,  @Param("shopId") Long shopId);



}
