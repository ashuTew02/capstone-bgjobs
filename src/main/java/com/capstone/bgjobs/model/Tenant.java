package com.capstone.bgjobs.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tenant")
public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String owner;
    private String repo;
    private String pat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getPat() {
        return pat;
    }

    public void setPat(String pat) {
        this.pat = pat;
    }

    public String getFindingEsIndex() {
        return findingEsIndex;
    }

    public void setFindingEsIndex(String findingEsIndex) {
        this.findingEsIndex = findingEsIndex;
    }

    // public LocalDateTime getCreatedAt() {
    //     return createdAt;
    // }

    // public void setCreatedAt(LocalDateTime createdAt) {
    //     this.createdAt = createdAt;
    // }

    // public LocalDateTime getUpdatedAt() {
    //     return updatedAt;
    // }

    // public void setUpdatedAt(LocalDateTime updatedAt) {
    //     this.updatedAt = updatedAt;
    // }

    // public List<UserTenantMapping> getUserTenantMappings() {
    //     return userTenantMappings;
    // }

    // public void setUserTenantMappings(List<UserTenantMapping> userTenantMappings) {
    //     this.userTenantMappings = userTenantMappings;
    // }

    @Column(name = "finding_es_index")
    private String findingEsIndex;

    // @Column(nullable = false, updatable = false, insertable = false,
    //         columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    // private LocalDateTime createdAt;

    // @Column(nullable = false,
    //         columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    // private LocalDateTime updatedAt;

    // One tenant can have many user-tenant mappings
    // @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<UserTenantMapping> userTenantMappings;

    // Getters, Setters
    
    /*
       e.g. a convenience method to get all Users or any special logic
    */
}
