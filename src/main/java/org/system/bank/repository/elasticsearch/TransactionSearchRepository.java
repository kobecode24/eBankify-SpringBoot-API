package org.system.bank.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.system.bank.document.TransactionDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import org.system.bank.enums.TransactionType;
import org.system.bank.repository.elasticsearch.base.BaseSearchRepository;

@Repository
public interface TransactionSearchRepository extends BaseSearchRepository<TransactionDocument, String> {

    List<TransactionDocument> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<TransactionDocument> findByAmountBetween(Double minAmount, Double maxAmount);

    @Query("{\"range\": {\"createdAt\": {\"gte\": \"?0\", \"lte\": \"?1\"}}}")
    List<TransactionDocument> findByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("{\"bool\": {\"must\": [" +
            "{\"range\": {\"amount\": {\"gte\": ?0, \"lte\": ?1}}}," +
            "{\"term\": {\"type\": \"?2\"}}," +
            "{\"range\": {\"createdAt\": {\"gte\": \"?3\", \"lte\": \"?4\"}}}" +
            "]}}")
    List<TransactionDocument> findByAmountRangeAndTypeAndDateRange(
            Double minAmount,
            Double maxAmount,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate);
}