package com.project.repository;

import com.project.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findTokenByValue(String value);

    void deleteByValue(String value);

    Token findByValue(String value);

    void deleteAllByUsername(String username);

    List<Token> findAllByUsername(String username);

    Boolean existsByValue(String value);

    @Query(value = "SELECT value FROM TOKEN WHERE expire_time <= current_date", nativeQuery = true)
    List<String> getListTokenExpired();

    @Modifying
    @Query(value = "DELETE FROM TOKEN WHERE expire_time <= current_date", nativeQuery = true)
    void clearTokenExpired();
}
