package com.taskflow.project_service.service;


import com.taskflow.project_service.clients.UserClient;
import com.taskflow.project_service.dto.AddMemberByEmailRequest;
import com.taskflow.project_service.dto.ProjectMemberRequestDTO;
import com.taskflow.project_service.dto.ProjectMemberResponseDTO;
import com.taskflow.project_service.dto.UserResponse;
import com.taskflow.project_service.entities.Project;
import com.taskflow.project_service.entities.ProjectMember;
import com.taskflow.project_service.enums.ProjectRole;
import com.taskflow.project_service.repository.ProjectMemberRepository;
import com.taskflow.project_service.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserClient umsClient;


    public List<ProjectMemberResponseDTO> getMembersByProject(UUID projectId) {
        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectMemberResponseDTO addMemberToProject(UUID projectId, ProjectMemberRequestDTO requestDTO) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        if (projectMemberRepository.findByProjectIdAndUserId(projectId, requestDTO.getUserId()).isPresent()) {
            throw new RuntimeException("User with id " + requestDTO.getUserId() + " is already a member of this project");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .userId(requestDTO.getUserId())
                .role(requestDTO.getRole())
                .build();

        ProjectMember savedMember = projectMemberRepository.save(member);
        return mapToResponseDTO(savedMember);
    }

    @Transactional
    public ProjectMemberResponseDTO addMemberByEmail(UUID projectId, String email, ProjectRole role, UUID requestingUserId, String authToken) {
        // 1. Check if requesting user has permission (must be OWNER or ADMIN)
        ProjectMember requestingMember = projectMemberRepository.findByProjectIdAndUserId(projectId, requestingUserId)
                .orElseThrow(() -> new RuntimeException("You are not a member of this project"));

        if (requestingMember.getRole() != ProjectRole.OWNER && requestingMember.getRole() != ProjectRole.ADMIN) {
            throw new RuntimeException("Only project owners and admins can add members");
        }

        // 2. Fetch user from UMS service using Feign client
        UserResponse userResponse;
        try {
            userResponse = umsClient.getUserByEmail(email, authToken);
        } catch (Exception e) {
            throw new RuntimeException("User not found with email: " + email);
        }

//         3. Check if user is already a member
        if (projectMemberRepository.findByProjectIdAndUserId(projectId, userResponse.getId()).isPresent()) {
            throw new RuntimeException("User with email " + email + " is already a member of this project");
        }

        // 4. Get project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        // 5. Create and save new member
        ProjectMember member = ProjectMember.builder()
                .project(project)
                .userId(userResponse.getId())
                .role(role)
                .build();

        ProjectMember savedMember = projectMemberRepository.save(member);
        return mapToResponseDTO(savedMember);
    }


    @Transactional
    public ProjectMemberResponseDTO updateMemberRole(UUID projectId, UUID userId, ProjectRole newRole) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in project with id: " + projectId + " and user id: " + userId));

        member.setRole(newRole);
        ProjectMember updatedMember = projectMemberRepository.save(member);
        return mapToResponseDTO(updatedMember);
    }

    @Transactional
    public void removeMemberFromProject(UUID projectId, UUID userId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in project with id: " + projectId + " and user id: " + userId));

        projectMemberRepository.delete(member);
    }

    private ProjectMemberResponseDTO mapToResponseDTO(ProjectMember member) {
        return ProjectMemberResponseDTO.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .projectId(member.getProject().getId())
                .role(member.getRole())
                .status(member.getStatus())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
