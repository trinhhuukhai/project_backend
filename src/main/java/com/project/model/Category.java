package com.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "create_date")
    @CreationTimestamp
    private Date createdDate;

//    @JsonIgnore
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Product> productList;


    @Column(name = "update_date")
    @UpdateTimestamp
    private Date updateDate;


}
