package com.taskflow.project_service.clients;

import com.taskflow.project_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ums-service")  
public interface UserClient {

    @GetMapping("/api/users/by-email")
    UserResponse getUserByEmail(@RequestParam("email") String email);

    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@org.springframework.web.bind.annotation.PathVariable("id") java.util.UUID id);

    @GetMapping("/api/users/by-keycloak-id/{keycloakId}")
    UserResponse getUserByKeycloakId(@org.springframework.web.bind.annotation.PathVariable("keycloakId") String keycloakId);
}