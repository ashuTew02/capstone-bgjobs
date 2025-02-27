package com.capstone.bgjobs.model.runbook;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "runbooks")
public class Runbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "enabled", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean enabled = true;  // default = true

    @Column(name = "created_at", nullable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // One Runbook can have multiple triggers
    @OneToMany(mappedBy = "runbook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunbookTrigger> triggers;

    // One Runbook can have multiple filters 
    @OneToMany(mappedBy = "runbook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunbookFilter> filters;

    // One Runbook can have multiple actions
    @OneToMany(mappedBy = "runbook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunbookAction> actions;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 1) Triggers
    public void addTrigger(RunbookTrigger trigger) {
        this.triggers.add(trigger);
        trigger.setRunbook(this);
    }

    public void removeTrigger(RunbookTrigger trigger) {
        this.triggers.remove(trigger);
        trigger.setRunbook(null);
    }
    // 2) Filters
    public void addFilter(RunbookFilter filter) {
        this.filters.add(filter);
        filter.setRunbook(this);
    }

    public void removeFilter(RunbookFilter filter) {
        this.filters.remove(filter);
        filter.setRunbook(null);
    }

    // 3) Actions
    public void addAction(RunbookAction action) {
        this.actions.add(action);
        action.setRunbook(this);
    }

    public void removeAction(RunbookAction action) {
        this.actions.remove(action);
        action.setRunbook(null);
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<RunbookTrigger> getTriggers() { return triggers; }
    public void setTriggers(List<RunbookTrigger> triggers) { this.triggers = triggers; }

    public List<RunbookFilter> getFilters() { return filters; }
    public void setFilters(List<RunbookFilter> filters) { this.filters = filters; }

    public List<RunbookAction> getActions() { return actions; }
    public void setActions(List<RunbookAction> actions) { this.actions = actions; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return "Runbook [id=" + id + ", tenantId=" + tenantId + ", name=" + name + ", description=" + description
                + ", enabled=" + enabled + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", triggers="
                + triggers + ", filters=" + filters + ", actions=" + actions + "]";
    }

    
}
