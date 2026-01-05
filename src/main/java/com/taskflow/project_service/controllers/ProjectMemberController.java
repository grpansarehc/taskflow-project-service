package com.taskflow.project_service.controllers;

import com.taskflow.project_service.entities.ProjectMember;
import com.taskflow.project_service.enums.ProjectRole;
import com.taskflow.project_service.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor

@Tag(name="Project Member Management",description="endpoints for member operations")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<ProjectMember>> getProjectMembers(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectMemberService.getMembersByProject(projectId));
    }

    @PostMapping
    public ResponseEntity<ProjectMember> addMember(
            @PathVariable Long projectId,
            @RequestParam Long userId,
            @RequestParam ProjectRole role) {
        return ResponseEntity.ok(projectMemberService.addMemberToProject(projectId, userId, role));
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<ProjectMember> updateRole(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestParam ProjectRole role) {
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, userId, role));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long projectId, @PathVariable Long userId) {
        projectMemberService.removeMemberFromProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }
}
