package com.taskflow.project_service.repository;

import com.taskflow.project_service.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {
}
