package io.spring.service.user;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.spring.infrastructure.util.exception.TokenRefreshException;
import io.spring.jparepos.user.JpaRefreshTokenRepository;
import io.spring.jparepos.user.JpaUserRepository;
import io.spring.model.model.entity.RefreshToken;

@Service
public class JpaRefreshTokenService {

	@Value("${jwt.jwtRefreshExpirationMs}")
	private Long refreshTokenDurationMs;

	@Autowired
	private JpaRefreshTokenRepository refreshTokenRepository;

	@Autowired
	private JpaUserRepository userRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken createRefreshToken(String userId) {
		RefreshToken refreshToken = new RefreshToken();

		// refreshToken.setUser(userRepository.findById(userId).get());
		refreshToken.setUserId(userId);
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
		refreshToken.setToken(UUID.randomUUID().toString());

		refreshToken = refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		// long minusMs = 1800000;
		long minusMs = 600000;
		long plusMs = 86400000;

		System.out.println("verifyExpiration");
		System.out.println(Instant.now());
		System.out.println(token.getExpiryDate().minusMillis(minusMs));
		System.out.println(token.getExpiryDate().minusMillis(minusMs).compareTo(Instant.now()));

		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			System.out.println("verifyExpiration11");
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(),
					"Refresh token was expired. Please make a new signin request");
		} else if (token.getExpiryDate().minusMillis(minusMs).compareTo(Instant.now()) < 0) {
			System.out.println("verifyExpiration22");
			token.setExpiryDate(Instant.now().plusMillis(plusMs));
			token = refreshTokenRepository.save(token);
		}

		return token;
	}

	@Transactional
	public int deleteByUserId(String userId) {
		return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
	}

}
