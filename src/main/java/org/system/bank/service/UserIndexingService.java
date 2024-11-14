package org.system.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.bank.config.IndexingProperties;
import org.system.bank.entity.User;
import org.system.bank.repository.jpa.UserRepository;
import org.system.bank.service.search.UserSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserIndexingService {
    private final UserRepository userRepository;
    private final UserSearchService userSearchService;
    private final IndexingProperties indexingProperties;

    @Transactional(readOnly = true)
    public void processUsers(int batchSize) {
        try {
            int page = 0;
            List<User> users;
            int totalIndexed = 0;

            do {
                Page<User> userPage = userRepository.findAllPaged(PageRequest.of(page, batchSize));
                if (!userPage.hasContent()) {
                    break;
                }

                users = userRepository.findAllWithAccountsByUsers(new ArrayList<>(userPage.getContent()));

                if (!users.isEmpty()) {
                    userSearchService.indexBulk(users);
                    totalIndexed += users.size();
                    log.debug("Indexed batch {} of users, total: {}", page + 1, totalIndexed);
                }

                page++;
                delay(indexingProperties.getDelayMillis());
            } while (!users.isEmpty());

            log.info("Indexed {} users", totalIndexed);
        } catch (Exception e) {
            log.error("Error processing users: ", e);
            throw new RuntimeException("Failed to process users", e);
        }
    }

    private void delay(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Indexing interrupted", e);
        }
    }
}