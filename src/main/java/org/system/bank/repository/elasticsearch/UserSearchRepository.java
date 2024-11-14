package org.system.bank.repository.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.stereotype.Repository;
import org.system.bank.document.UserDocument;
import org.system.bank.repository.elasticsearch.base.BaseSearchRepository;
import org.system.bank.enums.*;

import java.util.List;

@Repository
public interface UserSearchRepository extends BaseSearchRepository<UserDocument, String> {

    // Basic search methods
    List<UserDocument> findByNameContaining(String name);
    List<UserDocument> findByEmail(String email);
    List<UserDocument> findByRole(Role role);

    // Range searches
    List<UserDocument> findByAgeBetween(Integer minAge, Integer maxAge);
    List<UserDocument> findByMonthlyIncomeBetween(Double minIncome, Double maxIncome);
    List<UserDocument> findByCreditScoreGreaterThanEqual(Integer minScore);

    // Combined criteria searches
    @Query("{\"bool\": {\"must\": [" +
            "{\"range\": {\"creditScore\": {\"gte\": ?0}}}," +
            "{\"range\": {\"monthlyIncome\": {\"gte\": ?1}}}," +
            "{\"range\": {\"age\": {\"gte\": ?2}}}" +
            "]}}")
    List<UserDocument> findEligibleBorrowers(
            Integer minCreditScore,
            Double minMonthlyIncome,
            Integer minAge);

    // Full-text search
    @Query("{\"multi_match\": {" +
            "\"query\": \"?0\"," +
            "\"fields\": [\"name^3\", \"email^2\"]," +
            "\"type\": \"best_fields\"" +
            "}}")
    List<UserDocument> searchUsers(String query);
}