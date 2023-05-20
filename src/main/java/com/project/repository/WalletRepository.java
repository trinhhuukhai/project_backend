package com.project.repository;

import com.project.model.CartItem;
import com.project.model.User;
import com.project.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {

    Wallet findByUserId(Long id);

}
