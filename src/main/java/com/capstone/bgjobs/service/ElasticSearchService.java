package com.capstone.bgjobs.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.capstone.bgjobs.exception.ElasticsearchOperationException;
import com.capstone.bgjobs.model.*;
import com.capstone.bgjobs.repository.TenantRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {

    private final ElasticsearchClient esClient;
    private final TenantRepository tenantRepository;

    public ElasticSearchService(
            ElasticsearchClient esClient,
            TenantRepository tenantRepository
    ) {
        this.esClient = esClient;
        this.tenantRepository = tenantRepository;
    }

    /**
     * Save a single finding into the tenant-specific index.
     */
    public void saveFinding(Finding finding, Long tenantId) {
        String indexName = getIndexNameForTenant(tenantId);

        try {
            esClient.index(i -> i
                .index(indexName)
                .id(finding.getId())
                .document(finding)
            );
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Error saving finding to Elasticsearch.", e);
        }
    }

    // ============================================================
    //                 GET SINGLE FINDING BY ID
    // ============================================================
    public Finding getFindingById(String id, Long tenantId) {
        String indexName = getIndexNameForTenant(tenantId);

        try {
            var boolQuery = new FindingSearchQueryBuilder()
                    .withId(id)
                    .build();

            SearchResponse<Finding> response = esClient.search(s -> s
                    .index(indexName)
                    .query(q -> q.bool(boolQuery))
                    .size(1),
                Finding.class
            );

            List<Finding> findings = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            if (findings.isEmpty()) {
                return null;
            } else {
                return findings.get(0);
            }
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Can't find the given finding in tenant's index.", e);
        }
    }

    // ============================================================
    //            UPDATE A FINDING'S STATE (PATCH)
    // ============================================================
    public void updateFindingStateByFindingId(String id, FindingState state, Long tenantId) {
        String indexName = getIndexNameForTenant(tenantId);

        try {
            // 1) Get the finding in the tenant's index
            Finding finding = getFindingById(id, tenantId);
            if (finding == null) {
                throw new ElasticsearchOperationException("Finding not found with ID: " + id);
            }

            // 2) Update
            finding.setState(state);
            finding.setUpdatedAt(LocalDateTime.now().toString());

            // 3) Re-index
            esClient.index(i -> i
                .index(indexName)
                .id(finding.getId())
                .document(finding)
            );
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Error updating finding state in Elasticsearch.", e);
        }
    }

    private String getIndexNameForTenant(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ElasticsearchOperationException("Tenant not found with ID: " + tenantId));

        String indexName = tenant.getFindingEsIndex();
        if (indexName == null || indexName.isBlank()) {
            throw new ElasticsearchOperationException("Tenant " + tenantId + " has an invalid ES index name!");
        }
        return indexName;
    }
}
