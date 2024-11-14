package org.system.bank.service.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.system.bank.config.IndexingProperties;
import org.system.bank.document.TransactionDocument;
import org.system.bank.entity.Transaction;
import org.system.bank.repository.elasticsearch.TransactionSearchRepository;
import org.system.bank.service.search.base.BaseSearchService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionSearchService extends BaseSearchService<Transaction, TransactionDocument> {

    private final ElasticsearchOperations elasticsearchOperations;
    private final TransactionSearchRepository searchRepository;
    private final IndexingProperties indexingProperties;

    @Override
    protected ElasticsearchRepository<TransactionDocument, String> getRepository() {
        return searchRepository;
    }

    @Override
    protected ElasticsearchOperations getElasticsearchOperations() {
        return elasticsearchOperations;
    }

    @Override
    protected Class<TransactionDocument> getDocumentClass() {
        return TransactionDocument.class;
    }

    @Override
    protected IndexingProperties getIndexingProperties() {
        return indexingProperties;
    }

    @Override
    protected TransactionDocument convertToDocument(Transaction transaction) {
        TransactionDocument doc = TransactionDocument.builder()
                .id(transaction.getTransactionId().toString())
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .sourceAccountId(transaction.getSourceAccount().getAccountId())
                .destinationAccountId(transaction.getDestinationAccount().getAccountId())
                .sourceAccountHolder(transaction.getSourceAccount().getUser().getName())
                .destinationAccountHolder(transaction.getDestinationAccount().getUser().getName())
                .build();

        // Handle date conversion explicitly
        if (transaction.getCreatedAt() != null) {
            doc.setCreatedAt(transaction.getCreatedAt());
        }

        return doc;
    }

    public List<TransactionDocument> searchTransactions(String query, Pageable pageable) {
        try {
            Criteria criteria = new Criteria();

            if (StringUtils.hasText(query)) {
                criteria = criteria.or("sourceAccountHolder").contains(query)
                        .or("destinationAccountHolder").contains(query)
                        .or("amount").is(query)
                        .or("type").is(query)
                        .or("status").is(query);
            }

            Query searchQuery = new CriteriaQuery(criteria)
                    .setPageable(pageable)
                    .addSort(Sort.by(Sort.Direction.DESC, "createdAt"));

            SearchHits<TransactionDocument> hits = elasticsearchOperations.search(
                    searchQuery, TransactionDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching transactions: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<TransactionDocument> findByAmountRange(Double minAmount, Double maxAmount) {
        try {
            Criteria criteria = new Criteria("amount")
                    .greaterThanEqual(minAmount)
                    .lessThanEqual(maxAmount);

            CriteriaQuery searchQuery = new CriteriaQuery(criteria);
            SearchHits<TransactionDocument> hits = elasticsearchOperations.search(
                    searchQuery, TransactionDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding transactions by amount range: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<TransactionDocument> findByDateRange(LocalDateTime start, LocalDateTime end) {
        try {
            Criteria criteria = new Criteria("createdAt")
                    .greaterThanEqual(start)
                    .lessThanEqual(end);

            CriteriaQuery searchQuery = new CriteriaQuery(criteria);
            SearchHits<TransactionDocument> hits = elasticsearchOperations.search(
                    searchQuery, TransactionDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding transactions by date range: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public void deleteTransactionDocument(Long transactionId) {
        try {
            searchRepository.deleteById(transactionId.toString());
            log.debug("Deleted transaction document: {}", transactionId);
        } catch (Exception e) {
            log.error("Error deleting transaction document {}: {}", transactionId, e.getMessage(), e);
        }
    }

    public void deleteAllTransactionDocuments() {
        try {
            searchRepository.deleteAll();
            log.info("Deleted all transaction documents");
        } catch (Exception e) {
            log.error("Error deleting all transaction documents: {}", e.getMessage(), e);
        }
    }

    public long getTransactionDocumentCount() {
        try {
            return searchRepository.count();
        } catch (Exception e) {
            log.error("Error getting transaction document count: {}", e.getMessage(), e);
            return 0;
        }
    }
}