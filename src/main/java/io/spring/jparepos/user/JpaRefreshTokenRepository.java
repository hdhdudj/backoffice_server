package io.spring.jparepos.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import io.spring.model.model.entity.RefreshToken;
import io.spring.model.model.entity.User;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	@Modifying
	int deleteByUser(User user);

}
