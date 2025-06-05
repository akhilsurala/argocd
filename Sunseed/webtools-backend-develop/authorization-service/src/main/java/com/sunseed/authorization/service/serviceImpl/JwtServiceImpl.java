package com.sunseed.authorization.service.serviceImpl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.sunseed.authorization.service.entity.User;
import com.sunseed.authorization.service.exceptions.AuthenticationException;
import com.sunseed.authorization.service.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

	@Value("${jwt.secret.key}")
	private String SECRET_KEY;

	@Value("${jwt.token.expiration.time}")
	private Long TOKEN_EXPIRATION_TIME;

	private final UserDetailsService userDetailsService;

	public String generateToken(UserDetails userDetails) {
		return JwtUtil.generateToken(userDetails, TOKEN_EXPIRATION_TIME, SECRET_KEY);
	}

	public String getUsernameFromJwtToken(String jwtToken) {
		return JwtUtil.extractUsername(jwtToken, SECRET_KEY);
	}

	public UserDetails validateToken(String jwtToken) {
		return JwtUtil.validateToken(jwtToken, userDetailsService, SECRET_KEY);
	}

	// static inner class for reducing the scope
	private static class JwtUtil {

		private static String generateToken(UserDetails userDetails, Long tokenExpirationTime, String secretKey) {

			Map<String, Object> claims = new HashMap<>();

			return generateToken(claims, userDetails, tokenExpirationTime, secretKey);
		}

		// further jti will be added to stop replay attacks will store that inside db
		// for allowlist and denylist
		// further nbf can also be used for scheduled token creation
		private static String generateToken(Map<String, Object> extraClaims, UserDetails userDetails,
				Long tokenExpirationTime, String secretKey) {

			String jwtToken = Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
					.setIssuedAt(new Date(System.currentTimeMillis())).setIssuer("Authorization Service")
					.setExpiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
					.signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256).compact();
			return jwtToken;
		}

		private static String extractUsername(String jwtToken, String secretKey) {
			return extractClaim(jwtToken, Claims::getSubject, secretKey);
		}

		private static Key getSignInKey(String secretKey) {
			byte[] keyBytes = Decoders.BASE64.decode(secretKey);
			return Keys.hmacShaKeyFor(keyBytes);
		}

		private static Claims extractAllClaims(String jwtToken, String secretKey) {

			Claims claims = Jwts.parserBuilder().setSigningKey(getSignInKey(secretKey)).build().parseClaimsJws(jwtToken)
					.getBody();
			return claims;
		}

		private static <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver, String secretKey) {
			final Claims claims = extractAllClaims(jwtToken, secretKey);
			return claimsResolver.apply(claims);
		}

		private static UserDetails validateToken(String jwtToken, UserDetailsService userDetailsService,
				String secretKey) {

			Claims claims = extractAllClaims(jwtToken, secretKey);

			String username = claims.getSubject();
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			User user = (User) userDetails;
			if (user != null && user.getIsActive() == false)
				throw new AuthenticationException("Unauthorized: User is unauthorized", HttpStatus.FORBIDDEN);
			return userDetails;
		}
	}
}
