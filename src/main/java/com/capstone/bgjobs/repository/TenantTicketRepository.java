package com.capstone.bgjobs.repository;

import com.capstone.bgjobs.model.TenantTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantTicketRepository extends JpaRepository<TenantTicket, Long> {
    
    Optional<TenantTicket> findByEsFindingId(String esFindingId);
    
    // Retrieve all TenantTicket records for a specific tenant
    List<TenantTicket> findAllByTenantId(Long tenantId);

    void deleteByEsFindingId(String esFindingId);
}

