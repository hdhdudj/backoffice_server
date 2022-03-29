package io.spring.infrastructure.util;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.spring.dao.user.User;
import io.spring.service.JwtService;

@Component
public class DefaultJwtService implements JwtService {
    private String secret;
    private int sessionTime;
	private long refreshTime;

    @Autowired
    public DefaultJwtService(@Value("${jwt.secret}") String secret,
			@Value("${jwt.sessionTime}") int sessionTime, @Value("${jwt.jwtRefreshExpirationMs}") long refreshTime) {
        this.secret = secret;
        this.sessionTime = sessionTime;
		this.refreshTime = refreshTime;
    }

    @Override
    public String toToken(User user) {
        return Jwts.builder()
            .setSubject(user.getId())
            .setExpiration(expireTimeFromNow())
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

	@Override
	public String toRefreshToken(User user) {

		// return
		// Jwts.builder().setSubject(user.getId()).setExpiration(ExpireTimeFromNow_RefreshToken())
		return Jwts.builder().setSubject(user.getId()).setExpiration(expireTimeFromNow())
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

    @Override
    public Optional<String> getSubFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

			System.out.println(claimsJws.getBody().getExpiration());

			// Jwt jw = Jwts.parser().setSigningKey(secret).parse(token);

			// System.out.println(claim);

            return Optional.ofNullable(claimsJws.getBody().getSubject());
        } catch (Exception e) {

			e.printStackTrace();

            return Optional.empty();
        }
    }

    private Date expireTimeFromNow() {

		System.out.println(sessionTime);
		return new Date(System.currentTimeMillis() + sessionTime);
    }

	private Date ExpireTimeFromNow_RefreshToken() {
		return new Date(System.currentTimeMillis() + refreshTime);
	}

}
