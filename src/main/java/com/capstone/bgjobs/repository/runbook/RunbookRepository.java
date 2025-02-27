package com.capstone.bgjobs.repository.runbook;

import com.capstone.bgjobs.model.runbook.Runbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunbookRepository extends JpaRepository<Runbook, Long> {
    List<Runbook> findByTenantId(Long tenantId);
}
