package org.system.bank.repository.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;
import org.system.bank.document.InvoiceDocument;
import org.system.bank.repository.elasticsearch.base.BaseSearchRepository;
import org.system.bank.enums.InvoiceStatus;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceSearchRepository extends BaseSearchRepository<InvoiceDocument, String> {

    // Basic search methods
    List<InvoiceDocument> findByUserId(Long userId);

    List<InvoiceDocument> findByStatus(InvoiceStatus status);

    List<InvoiceDocument> findByUserName(String userName);

    // Amount-based searches
    List<InvoiceDocument> findByAmountDueBetween(Double minAmount, Double maxAmount);

    // Date-based searches
    List<InvoiceDocument> findByDueDateBefore(LocalDate date);

    List<InvoiceDocument> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    // Combined searches
    @Query("{\"bool\": {\"must\": [" +
            "{\"term\": {\"status\": \"PENDING\"}}," +
            "{\"range\": {\"dueDate\": {\"lt\": \"now\"}}}," +
            "{\"range\": {\"amountDue\": {\"gt\": 0}}}" +
            "]}}")
    List<InvoiceDocument> findOverdueInvoices();

    // Aggregation queries
    @Query("{\"aggs\": {" +
            "\"total_due\": {" +
            "\"sum\": {\"field\": \"amountDue\"}" +
            "}," +
            "\"avg_amount\": {" +
            "\"avg\": {\"field\": \"amountDue\"}" +
            "}" +
            "}}")
    SearchHits<InvoiceDocument> getInvoiceStatistics();
}