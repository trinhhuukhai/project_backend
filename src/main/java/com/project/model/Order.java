package com.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "order_date")
    @CreationTimestamp
    private Date orderDate;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItemList;


    @Column(name = "update_date")
    @UpdateTimestamp
    private Date updateDate;

    private String status;

    private String paymentStatus;

    private Double total;

//    private Double billInvoice;
//
//    private Double tax;

//    private Double invest;

    public void updateStatus() {
        boolean allConfirmed = true;
        for (OrderItem item : orderItemList) {
            if (!"Đã xác nhận".equals(item.getStatus())) {
                allConfirmed = false;
                break;
            }
        }
        if (allConfirmed) {
            setStatus("Đã xác nhận");
        }
    }

    public void updateStatusPayment() {
        boolean allConfirmed = true;
        for (OrderItem item : orderItemList) {
            if (!"Hoàn tiền".equals(item.getPaymentStatus())) {
                allConfirmed = false;
                break;
            }
        }
        if (allConfirmed) {
            setPaymentStatus("Hoàn tiền");
        }
    }


}
