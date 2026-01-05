package com.taskflow.project_service.repository;

import com.taskflow.project_service.entities.WorkFlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkFlowStatusRepository extends JpaRepository<WorkFlowStatus,Long> {

    public List<WorkFlowStatus> findByProjectId(Long projectId);
}
