package com.quan.apartment_building_management_system.config;

import com.quan.apartment_building_management_system.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    public WebMvcConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/admin/**", "/manager/**", "/resident/**", "/maintenance/**")
                .excludePathPatterns("/login", "/logout", "/forgot-password", "/reset-password", "/css/**", "/js/**", "/images/**", "/assets/**", "/fonts/**", "/webjars/**", "/api/**");
    }
}
