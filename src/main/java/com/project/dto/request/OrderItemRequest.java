package com.project.dto.request;

import com.project.model.Order;
import com.project.model.Product;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequest {

    private Long id;

    private Long orderId;

    private Long productId;

    private Long shopId;

    private String status;

    private String paymentStatus;

    private double price;

    private int quantity;

}
