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
import org.system.bank.document.UserDocument;
import org.system.bank.entity.User;
import org.system.bank.repository.elasticsearch.UserSearchRepository;
import org.system.bank.service.search.base.BaseSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSearchService extends BaseSearchService<User, UserDocument> {

    private final ElasticsearchOperations elasticsearchOperations;
    private final UserSearchRepository searchRepository;
    private final IndexingProperties indexingProperties;

    @Override
    protected ElasticsearchRepository<UserDocument, String> getRepository() {
        return searchRepository;
    }

    @Override
    protected ElasticsearchOperations getElasticsearchOperations() {
        return elasticsearchOperations;
    }

    @Override
    protected Class<UserDocument> getDocumentClass() {
        return UserDocument.class;
    }

    @Override
    protected IndexingProperties getIndexingProperties() {
        return indexingProperties;
    }

    @Override
    protected UserDocument convertToDocument(User user) {
        List<String> accountIds = new ArrayList<>();
        if (user.getAccounts() != null) {
            accountIds = user.getAccounts().stream()
                    .map(account -> account.getAccountId().toString())
                    .collect(Collectors.toList());
        }

        return UserDocument.builder()
                .id(user.getUserId().toString())
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .monthlyIncome(user.getMonthlyIncome())
                .creditScore(user.getCreditScore())
                .role(user.getRole())
                .accountIds(accountIds)
                .build();
    }
    public List<UserDocument> searchUsers(String query, Pageable pageable) {
        try {
            Criteria criteria = new Criteria();

            if (StringUtils.hasText(query)) {
                criteria = criteria.or("name").contains(query)
                        .or("email").contains(query)
                        .or("role").is(query);
            }

            CriteriaQuery searchQuery = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<UserDocument> hits = elasticsearchOperations.search(
                    searchQuery, UserDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching users: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<UserDocument> findEligibleBorrowers(Integer minCreditScore,
                                                    Double minMonthlyIncome,
                                                    Integer minAge) {
        try {
            Criteria criteria = new Criteria()
                    .and("creditScore").greaterThanEqual(minCreditScore)
                    .and("monthlyIncome").greaterThanEqual(minMonthlyIncome)
                    .and("age").greaterThanEqual(minAge);

            CriteriaQuery searchQuery = new CriteriaQuery(criteria);
            SearchHits<UserDocument> hits = elasticsearchOperations.search(
                    searchQuery, UserDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding eligible borrowers: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<UserDocument> findByIncomeRange(Double minIncome, Double maxIncome) {
        try {
            Criteria criteria = new Criteria("monthlyIncome")
                    .greaterThanEqual(minIncome)
                    .lessThanEqual(maxIncome);

            CriteriaQuery searchQuery = new CriteriaQuery(criteria);
            SearchHits<UserDocument> hits = elasticsearchOperations.search(
                    searchQuery, UserDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding users by income range: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<UserDocument> findByCreditScoreRange(Integer minScore, Integer maxScore) {
        try {
            Criteria criteria = new Criteria("creditScore")
                    .greaterThanEqual(minScore)
                    .lessThanEqual(maxScore);

            CriteriaQuery searchQuery = new CriteriaQuery(criteria);
            SearchHits<UserDocument> hits = elasticsearchOperations.search(
                    searchQuery, UserDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding users by credit score range: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public void deleteUserDocument(Long userId) {
        try {
            searchRepository.deleteById(userId.toString());
            log.debug("Deleted user document: {}", userId);
        } catch (Exception e) {
            log.error("Error deleting user document {}: {}", userId, e.getMessage(), e);
        }
    }

    public void deleteAllUserDocuments() {
        try {
            searchRepository.deleteAll();
            log.info("Deleted all user documents");
        } catch (Exception e) {
            log.error("Error deleting all user documents: {}", e.getMessage(), e);
        }
    }

    public long getUserDocumentCount() {
        try {
            return searchRepository.count();
        } catch (Exception e) {
            log.error("Error getting user document count: {}", e.getMessage(), e);
            return 0;
        }
    }
}