package com.office.yancao.untils;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")        // 拦截所有 /api/ 开头的请求
                .allowedOriginPatterns("http://localhost:5173",
                        "http://localhost:8085",
                        "http://192.168.175.185",   //
                        "http://127.0.0.1") // 允许前端域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)       // 允许携带 cookie
                .maxAge(3600);                // 预检请求缓存 1 小时
    }
}