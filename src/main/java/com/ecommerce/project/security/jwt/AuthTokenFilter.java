package com.ecommerce.project.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsService userDetailsService;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);


/**
 * Filters incoming HTTP requests to authenticate users based on JWT.
 *
 * This method retrieves the JWT from the request header, validates the token,
 * extracts the username, loads the user details, and sets the authentication
 * context if the token is valid. If any error occurs during the authentication
 * process, it logs the error. The method then passes the request and response
 * to the next filter in the filter chain.
 *
 * @param request the HTTP request to be filtered
 * @param response the HTTP response associated with the request
 * @param filterChain the filter chain to pass the request and response to the next filter
 * @throws ServletException if an exception is thrown during filtering
 * @throws IOException if an I/O error occurs during filtering
 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwt);

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userDetails,
						null,
						userDetails.getAuthorities());
				logger.debug("Roles from JWT: {}", userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * Extracts the JSON Web Token (JWT) from the Authorization header of the HTTP request.
	 *
	 * This method retrieves the JWT token from the request's "Authorization" header
	 * using the jwtUtils utility. It logs the extracted token for debugging purposes.
	 *
	 * @param request the HTTP request containing the JWT in the Authorization header
	 * @return the extracted JWT string, or null if the token is not present or invalid
	 */
	private String parseJwt(HttpServletRequest request) {
//		String jwt = jwtUtils.getJwtFromHeader(request); // Extract JWT from Authorization header
			String jwt = jwtUtils.getJwtCookie(request); // Extract JWT from cookie
		logger.debug("AuthTokenFilter.java: {}", jwt);
		return jwt;
	}
}
