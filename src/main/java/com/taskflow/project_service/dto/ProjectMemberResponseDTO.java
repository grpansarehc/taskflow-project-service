package com.taskflow.project_service.dto;

import com.taskflow.project_service.enums.MemberStatus;
import com.taskflow.project_service.enums.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberResponseDTO {
    private UUID id;
    private UUID userId;
    private UUID projectId;
    private ProjectRole role;
    private MemberStatus status;
    private LocalDateTime joinedAt;
}
