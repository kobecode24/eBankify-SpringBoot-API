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
import org.system.bank.document.LoanDocument;
import org.system.bank.entity.Loan;
import org.system.bank.repository.elasticsearch.LoanSearchRepository;
import org.system.bank.service.search.base.BaseSearchService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanSearchService extends BaseSearchService<Loan, LoanDocument> {

    private final ElasticsearchOperations elasticsearchOperations;
    private final LoanSearchRepository searchRepository;
    private final IndexingProperties indexingProperties;

    @Override
    protected ElasticsearchRepository<LoanDocument, String> getRepository() {
        return searchRepository;
    }

    @Override
    protected ElasticsearchOperations getElasticsearchOperations() {
        return elasticsearchOperations;
    }

    @Override
    protected Class<LoanDocument> getDocumentClass() {
        return LoanDocument.class;
    }

    @Override
    protected IndexingProperties getIndexingProperties() {
        return indexingProperties;
    }

    @Override
    @Transactional(readOnly = true)
    protected LoanDocument convertToDocument(Loan entity) {
        return LoanDocument.builder()
                .id(entity.getLoanId().toString())
                .loanId(entity.getLoanId())
                .principal(entity.getPrincipal())
                .interestRate(entity.getInterestRate())
                .termMonths(entity.getTermMonths())
                .monthlyPayment(entity.getMonthlyPayment())
                .remainingAmount(entity.getRemainingAmount())
                .status(entity.getStatus())
                .userId(entity.getUser().getUserId())
                .userName(entity.getUser().getName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .guarantees(entity.getGuarantees())
                .build();
    }



    /*public void indexLoan(Loan loan) {
        try {
            LoanDocument document = buildLoanDocument(loan);
            saveDocument(document);
            log.debug("Indexed loan: {}", loan.getLoanId());
        } catch (Exception e) {
            log.error("Error indexing loan {}: {}", loan.getLoanId(), e.getMessage(), e);
        }
    }*/

    public List<LoanDocument> searchLoans(String query, Pageable pageable) {
        try {
            Criteria criteria = new Criteria();

            if (StringUtils.hasText(query)) {
                criteria = criteria.or("userName").contains(query)
                        .or("guarantees").contains(query);
            }

            CriteriaQuery searchQuery = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<LoanDocument> hits = elasticsearchOperations.search(
                    searchQuery, LoanDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching loans: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<LoanDocument> findOverdueLoans() {
        try {
            return searchRepository.findOverdueLoans();
        } catch (Exception e) {
            log.error("Error finding overdue loans: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private LoanDocument buildLoanDocument(Loan loan) {
        return LoanDocument.builder()
                .id(loan.getLoanId().toString())
                .loanId(loan.getLoanId())
                .principal(loan.getPrincipal())
                .interestRate(loan.getInterestRate())
                .termMonths(loan.getTermMonths())
                .monthlyPayment(loan.getMonthlyPayment())
                .remainingAmount(loan.getRemainingAmount())
                .status(loan.getStatus())
                .userId(loan.getUser().getUserId())
                .userName(loan.getUser().getName())
                .startDate(loan.getStartDate())
                .endDate(loan.getEndDate())
                .guarantees(loan.getGuarantees())
                .build();
    }
}