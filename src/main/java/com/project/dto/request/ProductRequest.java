package com.project.dto.request;

import com.project.model.Category;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    private Long id;

    private String name;

    private String description;

    private Double in_price;

    private Double out_price;

    private String brand;

    private String color;

    private int inventory;

    private int sold;

    private Long categoryId;

    private Long shopId;

    private MultipartFile productImage;


}
