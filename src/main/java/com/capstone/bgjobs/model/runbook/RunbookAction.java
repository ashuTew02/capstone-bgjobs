package com.capstone.bgjobs.model.runbook;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.capstone.bgjobs.model.FindingState;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "runbook_actions")
public class RunbookAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // For "update" action, from/to states
    // @Column(name = "from_state")
    // private String fromState;

    @Column(name = "to_state")
    @Enumerated(EnumType.STRING)
    private FindingState toState;

    // Ticket creation flag
    @Column(name = "ticket_create")
    private boolean ticketCreate;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 50)
    private RunbookActionType actionType;

    public RunbookActionType getActionType() {
        return actionType;
    }

    public void setActionType(RunbookActionType actionType) {
        this.actionType = actionType;
    }

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

    // public String getFromState() { return fromState; }
    // public void setFromState(String fromState) { this.fromState = fromState; }

    public FindingState getToState() { return toState; }
    public void setToState(FindingState toState) { this.toState = toState; }

    public boolean isTicketCreate() { return ticketCreate; }
    public void setTicketCreate(boolean ticketCreate) { this.ticketCreate = ticketCreate; }

    public Runbook getRunbook() { return runbook; }
    public void setRunbook(Runbook runbook) { this.runbook = runbook; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
