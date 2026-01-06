package com.taskflow.project_service.service;

import com.taskflow.project_service.dto.ProjectRequestDTO;
import com.taskflow.project_service.dto.ProjectResponseDTO;
import com.taskflow.project_service.dto.WorkFlowStatusResponseDTO;
import com.taskflow.project_service.entities.Project;
import com.taskflow.project_service.entities.WorkFlowStatus;
import com.taskflow.project_service.enums.DefaultWorkflowStatus;
import com.taskflow.project_service.enums.ProjectRole;
import com.taskflow.project_service.repository.ProjectMemberRepository;
import com.taskflow.project_service.entities.ProjectMember;
import com.taskflow.project_service.repository.ProjectRepository;
import com.taskflow.project_service.repository.WorkFlowStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

        

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WorkFlowStatusRepository workFlowStatusRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProjectResponseDTO> getProjectById(UUID id) {
        return projectRepository.findById(id)
                .map(this::mapToResponseDTO);
    }

    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO requestDTO) {
        // 1️⃣ Map DTO to Entity and Save project
        Project project = Project.builder()
                .name(requestDTO.getName())
                .projectKey(requestDTO.getProjectKey().toUpperCase())
                .type(requestDTO.getType())
                .ownerId(requestDTO.getOwnerId())
                .build();
        
        Project savedProject = projectRepository.save(project);

        // 2️⃣ Create default workflow statuses
        List<WorkFlowStatus> statuses = Arrays.stream(DefaultWorkflowStatus.values())
                .map(defaultStatus -> WorkFlowStatus.builder()
                        .code(defaultStatus.getCode())
                        .statusName(defaultStatus.getName())
                        .orderIndex(defaultStatus.getOrder())
                        .isFinal(defaultStatus.isFinal())
                        .isActive(true)
                        .project(savedProject)
                        .build())
                .toList();

        workFlowStatusRepository.saveAll(statuses);

        // 3️⃣ Add the creator as the OWNER
        ProjectMember owner = ProjectMember.builder()
                .project(savedProject)
                .userId(savedProject.getOwnerId())
                .role(ProjectRole.OWNER)
                .build();
        
        projectMemberRepository.save(owner);

        return mapToResponseDTO(savedProject);
    }

    public ProjectResponseDTO updateProject(UUID id, ProjectRequestDTO projectDetailsDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        
        project.setName(projectDetailsDTO.getName());
        project.setProjectKey(projectDetailsDTO.getProjectKey().toUpperCase());
        project.setType(projectDetailsDTO.getType());
        project.setOwnerId(projectDetailsDTO.getOwnerId());
        
        Project updatedProject = projectRepository.save(project);
        return mapToResponseDTO(updatedProject);
    }

    public void deleteProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        projectRepository.delete(project);
    }

    public List<WorkFlowStatusResponseDTO> getProjectStatuses(UUID projectId) {
        return workFlowStatusRepository.findByProjectId(projectId).stream()
                .map(this::mapToStatusResponseDTO)
                .collect(Collectors.toList());
    }

    private ProjectResponseDTO mapToResponseDTO(Project project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .projectKey(project.getProjectKey())
                .type(project.getType())
                .ownerId(project.getOwnerId())
                .createdAt(project.getCreatedAt())
                .build();
    }

    private WorkFlowStatusResponseDTO mapToStatusResponseDTO(WorkFlowStatus status) {
        return WorkFlowStatusResponseDTO.builder()
                .id(status.getId())
                .statusName(status.getStatusName())
                .code(status.getCode())
                .description(status.getDescription())
                .orderIndex(status.getOrderIndex())
                .isFinal(status.getIsFinal())
                .isActive(status.getIsActive())
                .createdAt(status.getCreatedAt())
                .build();
    }
}
