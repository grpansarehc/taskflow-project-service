package com.taskflow.project_service.clients;

import com.taskflow.project_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/internal/users/by-email")
    UserResponse getUserByEmail(@RequestParam String email,String authToken);
}
