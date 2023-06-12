package com.project.dto.request;
import com.project.model.Product;
import com.project.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderFromCart {

    private Long id;

    private Long orderId;

    private Long userId;

    private Long customerId;

    private Product product;

    private double price;

    private Double total;

    private Double billInvoice;

    private Double tax;

    private Double invest;

    private int quantity;

    private String status;

    private String paymentStatus;

    private Shop shop;

    private String size;


}
