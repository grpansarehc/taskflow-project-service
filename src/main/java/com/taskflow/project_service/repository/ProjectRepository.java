package com.taskflow.project_service.repository;

import com.taskflow.project_service.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project,UUID> {
}
