package org.system.bank.service.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.system.bank.config.IndexingProperties;
import org.system.bank.document.AccountDocument;
import org.system.bank.entity.Account;
import org.system.bank.enums.AccountStatus;
import org.system.bank.repository.elasticsearch.AccountSearchRepository;
import org.system.bank.service.search.base.BaseSearchService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountSearchService extends BaseSearchService<Account, AccountDocument> {

    private final ElasticsearchOperations elasticsearchOperations;
    private final AccountSearchRepository searchRepository;
    private final IndexingProperties indexingProperties;

    @Override
    protected ElasticsearchRepository<AccountDocument, String> getRepository() {
        return searchRepository;
    }

    @Override
    protected ElasticsearchOperations getElasticsearchOperations() {
        return elasticsearchOperations;
    }

    @Override
    protected Class<AccountDocument> getDocumentClass() {
        return AccountDocument.class;
    }

    @Override
    protected IndexingProperties getIndexingProperties() {
        return indexingProperties;
    }

    @Override
    @Transactional(readOnly = true)
    protected AccountDocument convertToDocument(Account account) {
        return AccountDocument.builder()
                .id(account.getAccountId().toString())
                .accountId(account.getAccountId())
                .balance(account.getBalance())
                .status(account.getStatus())
                .userId(account.getUser().getUserId())
                .userName(account.getUser().getName())
                .build();
    }

    public List<AccountDocument> searchAccounts(String query, Pageable pageable) {
        try {
            Criteria criteria = new Criteria();

            if (StringUtils.hasText(query)) {
                criteria = criteria.or("userName").contains(query)
                        .or("status").is(query);
            }

            CriteriaQuery searchQuery = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<AccountDocument> hits = elasticsearchOperations.search(
                    searchQuery, AccountDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching accounts: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<AccountDocument> findByBalanceRange(Double minBalance, Double maxBalance) {
        try {
            Criteria criteria = new Criteria("balance")
                    .greaterThanEqual(minBalance)
                    .lessThanEqual(maxBalance);

            CriteriaQuery searchQuery = new CriteriaQuery(criteria);
            SearchHits<AccountDocument> hits = elasticsearchOperations.search(
                    searchQuery, AccountDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding accounts by balance range: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<AccountDocument> findByStatus(AccountStatus status) {
        try {
            Criteria criteria = new Criteria("status").is(status);
            CriteriaQuery searchQuery = new CriteriaQuery(criteria);
            SearchHits<AccountDocument> hits = elasticsearchOperations.search(
                    searchQuery, AccountDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding accounts by status: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<AccountDocument> findByUser(Long userId) {
        try {
            Criteria criteria = new Criteria("userId").is(userId);
            CriteriaQuery searchQuery = new CriteriaQuery(criteria);
            SearchHits<AccountDocument> hits = elasticsearchOperations.search(
                    searchQuery, AccountDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding accounts by user: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public void deleteAccountDocument(Long accountId) {
        try {
            searchRepository.deleteById(accountId.toString());
            log.debug("Deleted account document: {}", accountId);
        } catch (Exception e) {
            log.error("Error deleting account document {}: {}", accountId, e.getMessage(), e);
        }
    }

    public void deleteAllAccountDocuments() {
        try {
            searchRepository.deleteAll();
            log.info("Deleted all account documents");
        } catch (Exception e) {
            log.error("Error deleting all account documents: {}", e.getMessage(), e);
        }
    }

    public long getAccountDocumentCount() {
        try {
            return searchRepository.count();
        } catch (Exception e) {
            log.error("Error getting account document count: {}", e.getMessage(), e);
            return 0;
        }
    }
}
