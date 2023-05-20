package com.project.repository;

import com.project.model.OrderItem;
import com.project.model.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByName(String name);

    List<Product> findByCategoryId(Long categoryId);


    List<Product> findByShopId(Long shopId);

    @Query("SELECT p FROM Product p WHERE p.sold >= 50")
    List<Product> findBySoldGreaterThanOrEqual(@Param("sold") int sold);

    List<Product> findByNameStartingWith(String name, Sort createdDate);



}

