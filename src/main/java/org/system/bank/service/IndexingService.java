// IndexingService.java
package org.system.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class IndexingService {
    private final ElasticsearchIndexService elasticsearchIndexService;

    @Transactional(readOnly = true)
    public void reindexAll() {
        try {
            log.info("Starting Elasticsearch indexing...");
            elasticsearchIndexService.createIndices();
            elasticsearchIndexService.migrateExistingData();
            log.info("Completed Elasticsearch indexing");
        } catch (Exception e) {
            log.error("Error during reindexing: ", e);
            throw new RuntimeException("Failed to complete reindexing", e);
        }
    }

    public void reindexTransactions() {
        elasticsearchIndexService.reindexTransactions();
    }

    public void reindexUsers() {
        elasticsearchIndexService.reindexUsers();
    }

    public void reindexAccounts() {
        elasticsearchIndexService.reindexAccounts();
    }

    public void reindexLoans() {
        elasticsearchIndexService.reindexLoans();
    }

    public void reindexInvoices() {
        elasticsearchIndexService.reindexInvoices();
    }
}
