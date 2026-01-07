package com.taskflow.project_service.config;

import com.taskflow.project_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ums-service")
public interface UmsClient {

    @GetMapping("/api/users/by-email")
    UserResponse getUserByEmail(
        @RequestParam("email") String email,
        @RequestHeader("Authorization") String token
    );
}
