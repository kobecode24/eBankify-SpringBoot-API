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
import org.system.bank.document.InvoiceDocument;
import org.system.bank.entity.Invoice;
import org.system.bank.repository.elasticsearch.InvoiceSearchRepository;
import org.system.bank.service.search.base.BaseSearchService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceSearchService extends BaseSearchService<Invoice, InvoiceDocument> {

    private final ElasticsearchOperations elasticsearchOperations;
    private final InvoiceSearchRepository searchRepository;
    private final IndexingProperties indexingProperties;

    @Override
    protected ElasticsearchRepository<InvoiceDocument, String> getRepository() {
        return searchRepository;
    }

    @Override
    protected ElasticsearchOperations getElasticsearchOperations() {
        return elasticsearchOperations;
    }

    @Override
    protected Class<InvoiceDocument> getDocumentClass() {
        return InvoiceDocument.class;
    }

    @Override
    protected IndexingProperties getIndexingProperties() {
        return indexingProperties;
    }

    @Override
    @Transactional(readOnly = true)
    protected InvoiceDocument convertToDocument(Invoice entity) {
        return InvoiceDocument.builder()
                .id(entity.getInvoiceId().toString())
                .invoiceId(entity.getInvoiceId())
                .amountDue(entity.getAmountDue())
                .dueDate(entity.getDueDate())
                .paidDate(entity.getPaidDate())
                .status(entity.getStatus())
                .userId(entity.getUser().getUserId())
                .userName(entity.getUser().getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /*public void indexInvoice(Invoice invoice) {
        try {
            InvoiceDocument document = buildInvoiceDocument(invoice);
            saveDocument(document);
            log.debug("Indexed invoice: {}", invoice.getInvoiceId());
        } catch (Exception e) {
            log.error("Error indexing invoice {}: {}", invoice.getInvoiceId(), e.getMessage(), e);
        }
    }*/

    public List<InvoiceDocument> searchInvoices(String query, Pageable pageable) {
        try {
            Criteria criteria = new Criteria();

            if (StringUtils.hasText(query)) {
                criteria = criteria.or("userName").contains(query);
            }

            CriteriaQuery searchQuery = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<InvoiceDocument> hits = elasticsearchOperations.search(
                    searchQuery, InvoiceDocument.class);

            return hits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching invoices: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<InvoiceDocument> findOverdueInvoices() {
        try {
            return searchRepository.findOverdueInvoices();
        } catch (Exception e) {
            log.error("Error finding overdue invoices: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private InvoiceDocument buildInvoiceDocument(Invoice invoice) {
        return InvoiceDocument.builder()
                .id(invoice.getInvoiceId().toString())
                .invoiceId(invoice.getInvoiceId())
                .amountDue(invoice.getAmountDue())
                .dueDate(invoice.getDueDate())
                .paidDate(invoice.getPaidDate())
                .status(invoice.getStatus())
                .userId(invoice.getUser().getUserId())
                .userName(invoice.getUser().getName())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }
}