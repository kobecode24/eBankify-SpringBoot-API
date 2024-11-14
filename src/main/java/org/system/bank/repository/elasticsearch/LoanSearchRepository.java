package org.system.bank.repository.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.stereotype.Repository;
import org.system.bank.document.LoanDocument;
import org.system.bank.repository.elasticsearch.base.BaseSearchRepository;
import org.system.bank.enums.*;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanSearchRepository extends BaseSearchRepository<LoanDocument, String> {

    // Basic search methods
    List<LoanDocument> findByUserId(Long userId);
    List<LoanDocument> findByStatus(LoanStatus status);
    List<LoanDocument> findByUserName(String userName);

    // Amount-based searches
    List<LoanDocument> findByPrincipalBetween(Double minAmount, Double maxAmount);
    List<LoanDocument> findByRemainingAmountGreaterThan(Double minAmount);

    // Date-based searches
    List<LoanDocument> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<LoanDocument> findByEndDateBefore(LocalDate date);

    // Combined searches
    @Query("{\"bool\": {\"must\": [" +
            "{\"range\": {\"principal\": {\"gte\": ?0, \"lte\": ?1}}}," +
            "{\"term\": {\"status\": \"?2\"}}," +
            "{\"range\": {\"interestRate\": {\"lte\": ?3}}}" +
            "]}}")
    List<LoanDocument> findEligibleLoans(
            Double minAmount,
            Double maxAmount,
            LoanStatus status,
            Double maxInterestRate);

    // Overdue loans search
    @Query("{\"bool\": {\"must\": [" +
            "{\"term\": {\"status\": \"ACTIVE\"}}," +
            "{\"range\": {\"endDate\": {\"lt\": \"now\"}}}" +
            "]}}")
    List<LoanDocument> findOverdueLoans();
}