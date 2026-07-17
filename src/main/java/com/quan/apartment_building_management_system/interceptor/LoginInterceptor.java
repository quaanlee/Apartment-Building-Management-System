package com.quan.apartment_building_management_system.interceptor;

import com.quan.apartment_building_management_system.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);
        
        Account currentUser = (session != null) ? (Account) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect("/login");
            return false;
        }

        String role = currentUser.getRole().getRoleName().toUpperCase();

        // Check Admin routes
        if (requestURI.startsWith("/admin") && !"ADMIN".equals(role)) {
            response.sendRedirect("/login");
            return false;
        }

        // Check Manager routes
        if (requestURI.startsWith("/manager") && !"MANAGER".equals(role)) {
            response.sendRedirect("/login");
            return false;
        }

        // Check Resident routes
        if (requestURI.startsWith("/resident") && !"RESIDENT".equals(role)) {
            response.sendRedirect("/login");
            return false;
        }

        // Check Maintenance routes
        if (requestURI.startsWith("/maintenance") && !"MAINTENANCE STAFF".equals(role) && !"MAINTENANCE".equals(role)) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
