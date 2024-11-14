package org.system.bank.service.search.base;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.system.bank.config.IndexingProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class BaseSearchService<T, D> {

    protected abstract ElasticsearchRepository<D, String> getRepository();
    protected abstract ElasticsearchOperations getElasticsearchOperations();
    protected abstract Class<D> getDocumentClass();
    protected abstract IndexingProperties getIndexingProperties();

    public void createIndexIfNotExists() {
        try {
            if (!getElasticsearchOperations().indexOps(getDocumentClass()).exists()) {
                getElasticsearchOperations().indexOps(getDocumentClass()).create();
                log.info("Created index for {}", getDocumentClass().getSimpleName());
            }
        } catch (Exception e) {
            log.error("Error creating index for {}: {}", getDocumentClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Failed to create index", e);
        }
    }

    public void indexBulk(List<T> entities) {
        if (entities.isEmpty()) {
            return;
        }

        try {
            List<List<T>> batches = Lists.partition(entities, getIndexingProperties().getBatchSize());
            int totalIndexed = 0;
            int retryCount = 0;

            for (List<T> batch : batches) {
                boolean success = false;
                while (!success && retryCount < getIndexingProperties().getMaxRetries()) {
                    try {
                        List<D> documents = batch.stream()
                                .map(this::convertToDocument)
                                .toList();
                        getRepository().saveAll(documents);
                        totalIndexed += batch.size();
                        log.debug("Indexed batch of {} documents, total: {}", batch.size(), totalIndexed);
                        success = true;
                        delay();
                    } catch (Exception e) {
                        retryCount++;
                        if (retryCount >= getIndexingProperties().getMaxRetries()) {
                            throw e;
                        }
                        log.warn("Failed to index batch, attempt {}/{}. Retrying...",
                                retryCount, getIndexingProperties().getMaxRetries());
                        TimeUnit.MILLISECONDS.sleep(getIndexingProperties().getRetryDelayMillis());
                    }
                }
            }
            log.info("Successfully indexed {} documents of type {}",
                    totalIndexed, getDocumentClass().getSimpleName());
        } catch (Exception e) {
            log.error("Error bulk indexing documents: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to bulk index documents", e);
        }
    }

    protected abstract D convertToDocument(T entity);

    private void delay() {
        try {
            TimeUnit.MILLISECONDS.sleep(getIndexingProperties().getDelayMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Indexing interrupted", e);
        }
    }
}