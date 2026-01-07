package com.taskflow.project_service.controllers;

import com.taskflow.project_service.dto.AddMemberByEmailRequest;
import com.taskflow.project_service.dto.ProjectMemberRequestDTO;
import com.taskflow.project_service.dto.ProjectMemberResponseDTO;
import com.taskflow.project_service.entities.ProjectMember;
import com.taskflow.project_service.enums.ProjectRole;
import com.taskflow.project_service.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
@Tag(name="Project Member Management", description="Endpoints for member operations")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    @Operation(summary = "Get all members of a project")
    public ResponseEntity<List<ProjectMemberResponseDTO>> getProjectMembers(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectMemberService.getMembersByProject(projectId));
    }

    @PostMapping
    @Operation(summary = "Add a new member to a project")
    public ResponseEntity<ProjectMemberResponseDTO> addMember(
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectMemberRequestDTO requestDTO) {
        return ResponseEntity.ok(projectMemberService.addMemberToProject(projectId, requestDTO));
    }

    @PostMapping("/by-email")
    @Operation(summary = "Add a new member to a project by email")
    public ResponseEntity<ProjectMemberResponseDTO> addMemberByEmail(
            @PathVariable UUID projectId,
            @Valid @RequestBody AddMemberByEmailRequest requestDTO,
            @RequestHeader("Authorization") String authToken,
            @RequestHeader("X-User-Id") UUID requestingUserId) {
        return ResponseEntity.ok(
            projectMemberService.addMemberByEmail(
                projectId, 
                requestDTO.getEmail(), 
                requestDTO.getRole(), 
                requestingUserId, 
                authToken
            )
        );
    }


    @PutMapping("/{userId}/role")
    @Operation(summary = "Update member role")
    public ResponseEntity<ProjectMemberResponseDTO> updateRole(
            @PathVariable UUID projectId,
            @PathVariable UUID userId,
            @RequestParam ProjectRole role) {
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, userId, role));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Remove a member from a project")
    public ResponseEntity<Void> removeMember(@PathVariable UUID projectId, @PathVariable UUID userId) {
        projectMemberService.removeMemberFromProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }
}
