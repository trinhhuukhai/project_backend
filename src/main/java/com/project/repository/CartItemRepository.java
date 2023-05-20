package com.project.repository;

import com.project.model.CartItem;
import com.project.model.Product;
import com.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    List<CartItem> findByUser(User user);

    List<CartItem> findByUserId(Long userId);

//    CartItem findByProduct(Product product);


}
