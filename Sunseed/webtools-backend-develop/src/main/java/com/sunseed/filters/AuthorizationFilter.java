package com.sunseed.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.exceptions.AuthenticationException;
import com.sunseed.exceptions.GlobalExceptionHandler;
import com.sunseed.helper.WebClientResponseHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements Filter {

    private final GlobalExceptionHandler globalExceptionHandler;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${auth.url}")
    private String authorisationUrl;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        System.out.println("Entered here in AuthorizationFilter with requestURI = " + request.getRequestURI()
                + " and requestMethod = " + request.getMethod());

        // allow for websocket handshake
        if (request.getRequestURI().contains("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationException(null, "token.missing", HttpStatus.UNAUTHORIZED);
            }
//            String authUrl = "http://localhost:8081/auth/v1/authorize";
            String authUrl = authorisationUrl +"/auth/v1/authorize";
            WebClient webClient = WebClient.create(authUrl);
            Mono<ResponseEntity<Object>> authResponseMono = webClient.get().headers(headers -> {
                headers.add(HttpHeaders.AUTHORIZATION, authHeader);
                headers.add("coreRequestURI", request.getRequestURI());
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
                Object rolesObject = data.get("roles");
                List<String> rolesList = objectMapper.convertValue(rolesObject, new TypeReference<List<String>>() {
                });
                Set<String> roles = new HashSet<>(rolesList);

                request.setAttribute("emailId", emailId);
                request.setAttribute("userId", userId);
                request.setAttribute("roles", roles);
                request.setAttribute(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
                filterChain.doFilter(request, response);
                return;
            } else if (authResponse.getStatusCode() == HttpStatus.UNAUTHORIZED)
                throw new AuthenticationException(null, "unauthorized.user", HttpStatus.UNAUTHORIZED);
            else if (authResponse.getStatusCode() == HttpStatus.FORBIDDEN)
                throw new AuthenticationException(null, "invalid.token", HttpStatus.UNAUTHORIZED);
            else
                throw new AuthenticationException(null, "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            ResponseEntity<Object> finalResponse = globalExceptionHandler.authenticationExceptionHandler(e);
            response.setContentType("application/json");
            response.setStatus(finalResponse.getStatusCode().value());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(),
                    finalResponse.getBody());
            return;
        }
    }
}