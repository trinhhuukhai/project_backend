package com.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private Integer quantity;
    private Integer count;
    private Double total;
    private Double tax;
    private Double invoice;
    private List<ProductResponse> productResponses;
}
