package com.taskflow.project_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            // Forward Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                template.header("Authorization", authHeader);
            }
            
            // Forward X-User-Id header (optional, for additional context)
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader != null && !userIdHeader.isEmpty()) {
                template.header("X-User-Id", userIdHeader);
            }
            
            // Forward X-User-Email header (optional)
            String userEmailHeader = request.getHeader("X-User-Email");
            if (userEmailHeader != null && !userEmailHeader.isEmpty()) {
                template.header("X-User-Email", userEmailHeader);
            }
        }
    }
}
