package com.capstone.bgjobs.model.runbook;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "runbook_triggers")
public class RunbookTrigger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Example: "NEW_SCAN_INITIATE" or any future triggers
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", length = 50)
    private RunbookTriggerType triggerType;

    // // JSON config if your trigger requires extra config or data
    // @Column(name = "config", columnDefinition = "JSON")
    // private String config;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "runbook_id", nullable = false)
    @JsonIgnore
    private Runbook runbook;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RunbookTriggerType getTriggerType() { return triggerType; }
    public void setTriggerType(RunbookTriggerType triggerType) { this.triggerType = triggerType; }

    // public String getConfig() { return config; }
    // public void setConfig(String config) { this.config = config; }

    public Runbook getRunbook() { return runbook; }
    public void setRunbook(Runbook runbook) { this.runbook = runbook; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
