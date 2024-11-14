package org.system.bank.service;

public interface IndexingOperations {
    void createIndices();
    void migrateExistingData();
    void reindexTransactions();
    void reindexUsers();
    void reindexAccounts();
    void reindexLoans();
    void reindexInvoices();
    void processUsers(int batchSize);
}