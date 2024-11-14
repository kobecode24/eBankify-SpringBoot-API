package org.system.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.bank.config.IndexingProperties;
import org.system.bank.entity.*;
import org.system.bank.repository.jpa.*;
import org.system.bank.service.search.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchIndexService implements IndexingOperations {
    private final IndexingProperties indexingProperties;
    private final TransactionSearchService transactionSearchService;
    private final UserSearchService userSearchService;
    private final AccountSearchService accountSearchService;
    private final LoanSearchService loanSearchService;
    private final InvoiceSearchService invoiceSearchService;

    private final UserIndexingService userIndexingService;

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public void processUsers(int batchSize) {
        userIndexingService.processUsers(batchSize);
    }

    @Override
    public void createIndices() {
        log.info("Creating/verifying indices...");
        transactionSearchService.createIndexIfNotExists();
        userSearchService.createIndexIfNotExists();
        accountSearchService.createIndexIfNotExists();
        loanSearchService.createIndexIfNotExists();
        invoiceSearchService.createIndexIfNotExists();
    }

    @Override
    public void migrateExistingData() {
        try {
            List<Transaction> transactions = transactionRepository.findAll();
            if (!transactions.isEmpty()) {
                transactionSearchService.indexBulk(transactions);
                log.info("Indexed {} transactions", transactions.size());
                delay();
            }

            processUsers(indexingProperties.getBatchSize());

            List<Account> accounts = accountRepository.findAll();
            if (!accounts.isEmpty()) {
                accountSearchService.indexBulk(accounts);
                log.info("Indexed {} accounts", accounts.size());
                delay();
            }

            List<Loan> loans = loanRepository.findAll();
            if (!loans.isEmpty()) {
                loanSearchService.indexBulk(loans);
                log.info("Indexed {} loans", loans.size());
                delay();
            }

            List<Invoice> invoices = invoiceRepository.findAll();
            if (!invoices.isEmpty()) {
                invoiceSearchService.indexBulk(invoices);
                log.info("Indexed {} invoices", invoices.size());
            }
        } catch (Exception e) {
            log.error("Error during data migration: ", e);
            throw new RuntimeException("Failed to migrate data", e);
        }
    }

    @Override
    public void reindexTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        transactionSearchService.indexBulk(transactions);
        log.info("Re-indexed {} transactions", transactions.size());
    }

    @Override
    public void reindexUsers() {
        List<User> users = userRepository.findAll();
        userSearchService.indexBulk(users);
        log.info("Re-indexed {} users", users.size());
    }

    @Override
    public void reindexAccounts() {
        List<Account> accounts = accountRepository.findAll();
        accountSearchService.indexBulk(accounts);
        log.info("Re-indexed {} accounts", accounts.size());
    }

    @Override
    public void reindexLoans() {
        List<Loan> loans = loanRepository.findAll();
        loanSearchService.indexBulk(loans);
        log.info("Re-indexed {} loans", loans.size());
    }

    @Override
    public void reindexInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        invoiceSearchService.indexBulk(invoices);
        log.info("Re-indexed {} invoices", invoices.size());
    }

    private void delay() {
        try {
            TimeUnit.MILLISECONDS.sleep(indexingProperties.getDelayMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Indexing interrupted", e);
        }
    }
}