package org.system.bank.repository.elasticsearch.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.LocalDateTime;
import java.util.List;

@NoRepositoryBean
public interface BaseSearchRepository<T, ID> extends ElasticsearchRepository<T, ID> {

    @Query("{\"bool\": {\"must\": [{\"match_all\": {}}]}}")
    Page<T> findAllWithCustomQuery(Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"range\": {\"createdAt\": {\"gte\": \"?0\", \"lte\": \"?1\"}}}]}}")
    List<T> findByDateRange(LocalDateTime start, LocalDateTime end);
}
