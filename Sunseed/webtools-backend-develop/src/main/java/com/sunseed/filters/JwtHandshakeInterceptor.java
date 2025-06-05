package com.sunseed.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.sunseed.exceptions.AuthenticationException;
import com.sunseed.helper.WebClientResponseHelper;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
	
	@Value("${auth.url}")
    private String authorisationUrl;
	
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Extract token from query parameters
        String token = null;
        String requestUri = null;
        String username = null;
//        if (!(request instanceof ServletServerHttpRequest)) {
//            return false; // Skip non-WebSocket connections
//        }
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            requestUri = servletRequest.getRequestURI();
            token = servletRequest.getParameter("token");
            username = servletRequest.getParameter("username");

            System.out.println("Token from query parameter: " + token + "get username from client at the time of connection :" + username);
        }
        if (token != null) {

            // validate token using
//            String authUrl = "http://localhost:8081/auth/v1/authorize";
        	String authUrl = authorisationUrl + "/auth/v1/authorize";
            WebClient webClient = WebClient.create(authUrl);
            final String authHeader = token;
            final String requestURI = requestUri;
            System.out.println("in interceptor : token is =" + authHeader + " requestURI is =" + requestURI);
            Mono<ResponseEntity<Object>> authResponseMono = webClient.get().headers(headers -> {
                headers.add("Authorization", authHeader);
                headers.add("coreRequestURI", requestURI);
            }).retrieve().toEntity(Object.class).onErrorResume(WebClientResponseException.class, ex -> {
                if (WebClientResponseHelper.isAllowed(ex.getStatusCode())) {
                    return Mono
                            .just(ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class)));
                } else {
                    return Mono
                            .just(ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class)));
                }
            });
            ResponseEntity<Object> authResponse = authResponseMono.block();
            if (authResponse.getStatusCode() == HttpStatus.OK) {

                JsonNode data = WebClientResponseHelper.extractDataFromResponse(authResponse.getBody());
                String emailId = data.get("emailId").asText();
                Long userId = data.get("userId").asLong();
                //       Object rolesObject = data.get("roles");
                System.out.println("in interceptor emailId get from authroization service =" + emailId + " userId :" + userId);
                System.out.println("username for check :" + username);
                if (!emailId.equals(username)) {
                    throw new AuthenticationException(null, "username.not.equal", HttpStatus.FORBIDDEN);

                }
                System.out.println("set username in attribute:" + emailId);

                attributes.put("username", emailId);
                Principal userPrincipal = () -> emailId;
                attributes.put("principal", userPrincipal);
                return true;
            } else if (authResponse.getStatusCode() == HttpStatus.UNAUTHORIZED)
                throw new AuthenticationException(null, "unauthorized.user", HttpStatus.UNAUTHORIZED);
            else if (authResponse.getStatusCode() == HttpStatus.FORBIDDEN)
                throw new AuthenticationException(null, "invalid.token", HttpStatus.UNAUTHORIZED);
            else {
                System.out.println("exception in jwt handshakeinterceptor");
                throw new AuthenticationException(null, "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
