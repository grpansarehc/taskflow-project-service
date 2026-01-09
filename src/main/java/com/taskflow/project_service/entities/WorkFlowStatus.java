package com.taskflow.project_service.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_status", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WorkFlowStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "status_id")
    private UUID id;

    @Column(nullable = false, name = "status_name")
    private String statusName;

    @Column(nullable = false)
    private String code;   // e.g., TODO, IN_PROGRESS

    private String description;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Builder.Default
    @Column(name = "is_final")
    private Boolean isFinal = false;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
