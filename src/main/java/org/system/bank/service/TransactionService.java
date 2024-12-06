package org.system.bank.service;

import org.system.bank.dto.request.TransactionRequest;
import org.system.bank.dto.response.TransactionResponse;
import org.system.bank.entity.Transaction;
import org.system.bank.enums.TransactionStatus;
import org.system.bank.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);
    TransactionResponse getTransactionById(Long id);
    List<TransactionResponse> getAllTransactions();
    List<TransactionResponse> getTransactionsByAccount(Long accountId);
    List<TransactionResponse> getTransactionsByType(TransactionType type);
    List<TransactionResponse> getTransactionsByStatus(TransactionStatus status);
    List<TransactionResponse> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end);
    Double calculateDailyTransactions(Long accountId, LocalDateTime date);
    void processTransaction(Long transactionId);
    Double calculateTransactionFee(TransactionRequest request);
    boolean isTransactionValid(TransactionRequest request);
    List<TransactionResponse> getAccountTransactionHistory(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    Transaction getTransactionEntity(Long transactionId);
}