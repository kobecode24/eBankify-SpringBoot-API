package org.system.bank.repository.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.stereotype.Repository;
import org.system.bank.document.AccountDocument;
import org.system.bank.repository.elasticsearch.base.BaseSearchRepository;
import org.system.bank.enums.*;

import java.util.List;

@Repository
public interface AccountSearchRepository extends BaseSearchRepository<AccountDocument, String> {

    // Basic search methods
    List<AccountDocument> findByUserId(Long userId);
    List<AccountDocument> findByStatus(AccountStatus status);
    List<AccountDocument> findByUserName(String userName);

    // Balance-based searches
    List<AccountDocument> findByBalanceGreaterThan(Double minBalance);
    List<AccountDocument> findByBalanceLessThan(Double maxBalance);
    List<AccountDocument> findByBalanceBetween(Double minBalance, Double maxBalance);

    // Combined searches
    @Query("{\"bool\": {\"must\": [" +
            "{\"range\": {\"balance\": {\"gte\": ?0}}}," +
            "{\"term\": {\"status\": \"?1\"}}" +
            "]}}")
    List<AccountDocument> findActiveAccountsWithMinBalance(
            Double minBalance,
            AccountStatus status);

    // Full-text search
    @Query("{\"multi_match\": {" +
            "\"query\": \"?0\"," +
            "\"fields\": [\"userName^2\"]," +
            "\"type\": \"best_fields\"" +
            "}}")
    List<AccountDocument> searchAccounts(String query);
}