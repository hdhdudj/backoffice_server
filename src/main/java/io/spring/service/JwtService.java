package io.spring.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.spring.dao.user.User;

@Service
public interface JwtService {
    String toToken(User user);

	String toRefreshToken(User user);

    Optional<String> getSubFromToken(String token);
}
