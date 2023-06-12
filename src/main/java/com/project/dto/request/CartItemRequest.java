package com.project.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRequest {

    private Long id;

    private Long userId;

    private Long productId;

    private Long shopId;

    private Integer quantity;

    private Double price;

    private String size;
}
