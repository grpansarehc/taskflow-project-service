package com.taskflow.project_service.repository;

import com.taskflow.project_service.entities.WorkFlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkFlowStatusRepository extends JpaRepository<WorkFlowStatus,UUID> {

    public List<WorkFlowStatus> findByProjectId(UUID projectId);
}
