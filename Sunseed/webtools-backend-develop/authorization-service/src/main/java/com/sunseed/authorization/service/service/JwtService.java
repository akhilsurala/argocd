package com.sunseed.authorization.service.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

	String generateToken(UserDetails userDetails);
	String getUsernameFromJwtToken(String jwtToken);
	UserDetails validateToken(String jwtToken);
}
