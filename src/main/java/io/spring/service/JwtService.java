package io.spring.service;

import io.spring.dao.user.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface JwtService {
    String toToken(User user);

	String toRefreshToken(User user);

    Optional<String> getSubFromToken(String token);
}
