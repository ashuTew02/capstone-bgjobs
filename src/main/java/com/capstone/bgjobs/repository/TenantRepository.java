package com.capstone.bgjobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.capstone.bgjobs.model.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    // 1) By name
    Tenant findByName(String name);

    // 2) Example of retrieving just the PAT
    @Query("SELECT t.pat FROM Tenant t WHERE t.id = :tenantId")
    String findPatByTenantId(Long tenantId);

    // 3) Example of retrieving the Elasticsearch index name
    @Query("SELECT t.findingEsIndex FROM Tenant t WHERE t.id = :tenantId")
    String findEsIndexByTenantId(Long tenantId);

    /*
      ...whatever else you need for convenience
    */
}
