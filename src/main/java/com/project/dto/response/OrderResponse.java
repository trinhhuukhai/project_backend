package com.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private String customer;
    private String phone;
    private String address;
    private String email;
    private Date orderDate;
    private Double total;
    private Double billInvoice;
    private Double tax;
    private Double invest;
    private String status;
    private String paymentStatus;
    private List<OrderItemResponse> orderItemResponses;

}
