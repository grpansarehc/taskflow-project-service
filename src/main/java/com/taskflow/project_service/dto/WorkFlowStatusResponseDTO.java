package com.taskflow.project_service.dto;

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
public class WorkFlowStatusResponseDTO {
    private UUID id;
    private String statusName;
    private String code;
    private String description;
    private Integer position;
    private Boolean isFinal;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
