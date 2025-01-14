package com.ecommerce.project.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${spring.app.jwtSecret}")
	private String jwtSecret;

	@Value("${spring.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	/**
	 * Extracts the JWT from the Authorization header of the HTTP request.
	 *
	 * @param request the HttpServletRequest containing the Authorization header
	 * @return the JWT as a String if the Authorization header is present and starts with "Bearer ",
	 * otherwise returns null
	 */
	public String getJwtFromHeader(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		logger.debug("Authorization Header: {}", bearerToken);
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7); // Remove Bearer prefix
		}
		return null;
	}

	/**
	 * Generates a JWT token containing the given user's username.
	 *
	 * @param userDetails a Spring Security {@link UserDetails} containing the user's details
	 * @return a JWT token as a String, containing the user's username as the subject claim
	 */
	public String generateJwtToken(UserDetails userDetails) {
		String username = userDetails.getUsername();
		return Jwts.builder()
			.subject(username)
			.issuedAt(new Date())
			.expiration(new Date((new Date()).getTime() + jwtExpirationMs))
			.signWith(key())
			.compact();
	}

	/**
	 * Retrieves the username from a given JWT token.
	 *
	 * @param token a JWT token as a String
	 * @return the username as a String, or null if the token is invalid or doesn't contain a subject claim
	 */
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser()
			.verifyWith((SecretKey) key())
			.build().parseSignedClaims(token)
			.getPayload().getSubject();
	}

	/**
	 * Retrieves the secret key used for signing JWT tokens.
	 *
	 * @return a Key object used for HMAC-SHA signing of JWT tokens
	 */
	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	/**
	 * Validates a given JWT token.
	 *
	 * <p>This method verifies whether the given JWT token is valid and has not expired.
	 * If the token is valid, this method returns true. Otherwise, it returns false.
	 *
	 * @param authToken a JWT token as a String
	 * @return true if the JWT token is valid, false otherwise
	 */
	public boolean validateJwtToken(String authToken) {
		try {
			System.out.println("Validate");
			Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}
}