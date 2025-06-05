package com.sunseed.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.enums.EndpointStatus;
import com.sunseed.helper.UriMatcherHelper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestDelegationFilter implements Filter {

    private final UriMatcherHelper uriMatcherHelper;
    private final AuthorizationFilter authorizationFilter;
    private final MessageSource messageSource;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        System.out.println("Entered RequestDelegationFilter with requestURI= " + requestURI + " and requestMethod = "
                + requestMethod);

        // getting EndpointStatus
        EndpointStatus endpointStatus = uriMatcherHelper.getEndpointStatus(requestURI, requestMethod);
        System.out.println("Status of requested endpoint = " + endpointStatus);

        // if endpoint is allowed skipping auth filter and dispatching to servlet
        if (endpointStatus == EndpointStatus.ALLOWED || requestURI.contains("/swagger-ui")
                || requestURI.contains("/api-docs")) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(requestURI);
            requestDispatcher.forward(request, response);
        }
// allowed for websocket
        if (requestURI.contains("/ws")) {
            System.out.println("request for handshake for websocket in request delegation filter");
            filterChain.doFilter(request, response);
            return;
        }
        // if endpoint exists and not allowed delegate to AuthorizationFilter
        else if (endpointStatus == EndpointStatus.AUTH_REQUIRED) {
            authorizationFilter.doFilter(request, response, filterChain);
        }

        // sending 404 for not found status
        else if (endpointStatus == EndpointStatus.NOT_FOUND) {
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse.put("data", null);
            mapResponse.put("message",
                    messageSource.getMessage("resource.not.found", null, LocaleContextHolder.getLocale()));
            mapResponse.put("httpStatus", HttpStatus.NOT_FOUND);

            ResponseEntity<Object> finalResponse = new ResponseEntity<>(mapResponse, HttpStatus.NOT_FOUND);
            response.setContentType("application/json");
            response.setStatus(finalResponse.getStatusCode().value());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(),
                    finalResponse.getBody());
            return;
        }

    }
}
