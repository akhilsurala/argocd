package com.sunseed.authorization.service.filters;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.authorization.service.exceptions.AuthenticationException;
import com.sunseed.authorization.service.exceptions.GlobalExceptionHandler;
import com.sunseed.authorization.service.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final GlobalExceptionHandler globalExceptionHandler;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		System.out.println("Request: " + requestURI + " | Method: " + request.getMethod());
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		String jwtToken;
		if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		try {
			jwtToken = authHeader.toString().substring(7);
			if (SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = null;
				try {
					userDetails = jwtService.validateToken(jwtToken);
				} catch (MalformedJwtException | SignatureException | ExpiredJwtException | PrematureJwtException e) {
					throw new AuthenticationException("Unauthorized: Invalid token", HttpStatus.UNAUTHORIZED, e);
				} catch (UnsupportedJwtException e) {
					throw new AuthenticationException("Unauthorized: User is unauthorized", HttpStatus.UNAUTHORIZED, e);
				} catch (UsernameNotFoundException e) {
					throw new AuthenticationException("Unauthorized: User not found", HttpStatus.UNAUTHORIZED, e);
				} catch (Exception e) {
					throw new AuthenticationException("Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR, e);
				}

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
			filterChain.doFilter(request, response);
		} catch (AuthenticationException e) {
			ResponseEntity<Object> finalResponse = globalExceptionHandler.authenticationExceptionHandler(e);
			response.setContentType("application/json");
			response.setStatus(finalResponse.getStatusCode().value());
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(),
					finalResponse.getBody());
		}
	}

}
