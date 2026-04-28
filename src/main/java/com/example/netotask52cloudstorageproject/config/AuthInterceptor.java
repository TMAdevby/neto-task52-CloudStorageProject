
package com.example.netotask52cloudstorageproject.config;

import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import com.example.netotask52cloudstorageproject.service.AuthService;
import com.example.netotask52cloudstorageproject.model.User;
import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String requestUri = request.getRequestURI();

        if (requestUri.contains("/login")) {
            return true;
        }

        if (requestUri.contains("/logout")) {
            return true;
        }

        String token = request.getHeader("auth-token");

        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Требуется авторизация\", \"id\": 401}");
            return false;
        }

        Optional<User> user = authService.getUserByToken(token);

        if (user.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Недействительный токен\", \"id\": 401}");
            return false;
        }

        request.setAttribute("currentUser", user.get());

        return true;
    }
}
