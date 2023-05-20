package com.project.service;

import com.project.model.Token;
import com.project.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    @Transactional
    @CacheEvict(value = "token", key = "#token")
    public void deleteToken(String token) {
        tokenRepository.deleteByValue(token);
    }

    @Transactional
    @CacheEvict(value = "token", key = "#token")
    public void deleteAllByUsername(String username) {
        tokenRepository.deleteAllByUsername(username);
    }

    public List<Token> findAllByUsername(String username) {
        return tokenRepository.findAllByUsername(username);
    }

    @CachePut(value = "token", key = "#token.value")
    public String save(Token token) {
        token = tokenRepository.save(token);
        if (token.getTokenId() != null) {
            return token.getValue();
        }
        return "";
    }

    @Cacheable(value = "token", key = "#token")
    public String existsToken(String token) {
        Boolean exists = tokenRepository.existsByValue(token);
        if (exists) {
            return token;
        }
        return null;
    }

    public List<String> getListTokenExpired() {
        return tokenRepository.getListTokenExpired();
    }

    @Transactional
    public void clearTokenExpired() {
        tokenRepository.clearTokenExpired();
    }
}
