package com.project.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double inputPrice;
    private Double outputPrice;
    private String brand;
    private String color;
    private int inventory;
    private int sold;
    private String productImage;
    private Integer quantity;
    private Long cart_item_id;
    private Double Total;
}
