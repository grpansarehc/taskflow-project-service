package com.taskflow.project_service.dto;

import com.taskflow.project_service.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberRequestDTO {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Role is required")
    private ProjectRole role;
}
