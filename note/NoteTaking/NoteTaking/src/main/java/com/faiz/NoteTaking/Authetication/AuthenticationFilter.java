package com.faiz.NoteTaking.Authetication;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.faiz.NoteTaking.User.User;
import com.faiz.NoteTaking.User.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends HttpFilter {
    private final JsonWebToken jwt;
    private final UserService userService;

    private final List<String> allowedPaths = List.of(
            "api/v1/user/login",
            "api/v1/user/register",
            "api/v1/user/send-password-reset-token",
            "api/v1/user/reset-password");

    @Override
    protected void doFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = request.getRequestURI();
        if (allowedPaths.stream().anyMatch(path::contains)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new ServletException("Invalid authentication token");
            }
            String jwtToken = token.substring(7);
            String username = jwt.getUsername(jwtToken);
            User user = userService.findUser(username);
            request.setAttribute("authenticatedUser", user);
            chain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("Invalid authentication token");
            return;
        }
    }
}
