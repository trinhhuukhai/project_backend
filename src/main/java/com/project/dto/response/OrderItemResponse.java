package com.project.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    private Long id;
    private int quantity;
    private Double Total;
    private Double invest;
    private ProductResponse productResponses;
}
