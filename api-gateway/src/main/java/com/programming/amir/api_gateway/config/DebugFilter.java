package com.programming.amir.api_gateway.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@WebFilter("/*")
@Component
@Order(1)
public class DebugFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, jakarta.servlet.ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println("Request: " + req.getRequestURI() + ", Method: " + req.getMethod());
        chain.doFilter(request, response);
    }

}
