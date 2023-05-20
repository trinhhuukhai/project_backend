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
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "input-price")
    private Double inputPrice;

    @Column(name = "output-price")
    private Double outputPrice;

    @Column(name = "brand")
    private String brand;

    @Column(name = "color")
    private String color;

    @Column(name = "inventory")
    private int inventory;

    @Column(name = "sold")
    private int sold;
//
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private Shop shop;

//    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;


    @Column(name = "product_image")
    private String productImage;

    @Column(name = "create_date")
    @CreationTimestamp
    private Date createdDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImageList;

//    @JsonIgnoreProperties
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Review> reviewList;




    @Column(name = "update_date")
    @UpdateTimestamp
    private Date updateDate;

}
