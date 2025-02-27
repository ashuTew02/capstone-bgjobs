package com.capstone.bgjobs.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tenant_ticket")
public class TenantTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "ticket_id")
    private String ticketId;

    @Column(name = "es_finding_id", nullable = false, unique = true)
    private String esFindingId;

    // Constructors
    public TenantTicket() {
    }

    public TenantTicket(Tenant tenant, String ticketId, String esFindingId) {
        this.tenant = tenant;
        this.ticketId = ticketId;
        this.esFindingId = esFindingId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getEsFindingId() {
        return esFindingId;
    }

    public void setEsFindingId(String esFindingId) {
        this.esFindingId = esFindingId;
    }
}
