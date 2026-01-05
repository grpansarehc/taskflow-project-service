package com.taskflow.project_service.service;


import com.taskflow.project_service.entities.Project;
import com.taskflow.project_service.entities.ProjectMember;
import com.taskflow.project_service.enums.ProjectRole;
import com.taskflow.project_service.repository.ProjectMemberRepository;
import com.taskflow.project_service.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;

    public List<ProjectMember> getMembersByProject(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId);
    }

    public ProjectMember addMemberToProject(Long projectId, Long userId, ProjectRole role) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (projectMemberRepository.findByProjectIdAndUserId(projectId, userId).isPresent()) {
            throw new RuntimeException("User is already a member of this project");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .userId(userId)
                .role(role)
                .build();

        return projectMemberRepository.save(member);
    }

    public ProjectMember updateMemberRole(Long projectId, Long userId, ProjectRole newRole) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in project"));

        member.setRole(newRole);
        return projectMemberRepository.save(member);
    }

    public void removeMemberFromProject(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in project"));

        projectMemberRepository.delete(member);
    }
}
