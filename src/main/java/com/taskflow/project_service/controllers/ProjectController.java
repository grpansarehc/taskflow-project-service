package com.taskflow.project_service.controllers;

import com.taskflow.project_service.dto.ProjectRequestDTO;
import com.taskflow.project_service.dto.ProjectResponseDTO;
import com.taskflow.project_service.dto.WorkFlowStatusResponseDTO;
import com.taskflow.project_service.entities.Project;
import com.taskflow.project_service.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "Endpoints for project operations")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "Get all projects")
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable UUID id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO requestDTO) {
        return ResponseEntity.ok(projectService.createProject(requestDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable UUID id, @Valid @RequestBody ProjectRequestDTO projectDetailsDTO) {
        try {
            return ResponseEntity.ok(projectService.updateProject(id, projectDetailsDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/statuses")
    @Operation(summary = "Get workflow statuses for a project")
    public ResponseEntity<List<WorkFlowStatusResponseDTO>> getProjectStatuses(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectStatuses(id));
    }
}
