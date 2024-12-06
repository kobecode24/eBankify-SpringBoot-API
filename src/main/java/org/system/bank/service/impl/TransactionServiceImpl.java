package org.system.bank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.bank.dto.request.TransactionRequest;
import org.system.bank.dto.response.TransactionResponse;
import org.system.bank.entity.Account;
import org.system.bank.entity.Transaction;
import org.system.bank.enums.AccountStatus;
import org.system.bank.enums.TransactionStatus;
import org.system.bank.enums.TransactionType;
import org.system.bank.exception.InsufficientFundsException;
import org.system.bank.mapper.TransactionMapper;
import org.system.bank.repository.jpa.TransactionRepository;
import org.system.bank.service.AccountService;
import org.system.bank.service.TransactionService;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountService accountService;

    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        if (!isTransactionValid(request)) {
            throw new IllegalStateException("Invalid transaction request");
        }

        Transaction transaction = transactionMapper.toEntity(request);

        // Set the full account entities
        transaction.setSourceAccount(accountService.getAccountEntity(request.getSourceAccountId()));
        transaction.setDestinationAccount(accountService.getAccountEntity(request.getDestinationAccountId()));

        Transaction savedTransaction = transactionRepository.save(transaction);
        processTransaction(savedTransaction.getTransactionId());

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = findTransactionById(id);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        return transactionMapper.toResponseList(transactionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        Account account = accountService.getAccountEntity(accountId);
        return transactionMapper.toResponseList(
                transactionRepository.findBySourceAccountOrDestinationAccount(account, account)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByType(TransactionType type) {
        return transactionMapper.toResponseList(transactionRepository.findByType(type));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByStatus(TransactionStatus status) {
        return transactionMapper.toResponseList(transactionRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionMapper.toResponseList(transactionRepository.findByCreatedAtBetween(start, end));
    }

    @Override
    public Double calculateDailyTransactions(Long accountId, LocalDateTime date) {
        Account account = accountService.getAccountEntity(accountId);
        return transactionRepository.calculateTotalDebit(
                account,
                null,
                date.toLocalDate().atStartOfDay(),
                date.toLocalDate().atTime(23, 59, 59)
        );
    }

    @Override
    public boolean isTransactionValid(TransactionRequest request) {
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        Account sourceAccount = accountService.getAccountEntity(request.getSourceAccountId());
        Account destAccount = accountService.getAccountEntity(request.getDestinationAccountId());

        if (sourceAccount.getStatus() != AccountStatus.ACTIVE ||
                destAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("One or both accounts are not active");
        }

        double totalAmount = request.getAmount() + calculateTransactionFee(request);
        if (sourceAccount.getBalance() < totalAmount) {
            throw new InsufficientFundsException("Insufficient funds for transaction");
        }

        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        return true;
    }

    @Override
    public void processTransaction(Long transactionId) {
        Transaction transaction = findTransactionById(transactionId);

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction is not in PENDING state");
        }

        Account sourceAccount = accountService.getAccountEntity(transaction.getSourceAccount().getAccountId());
        Account destAccount = accountService.getAccountEntity(transaction.getDestinationAccount().getAccountId());

        double fee = transactionMapper.calculateFee(transaction);
        double totalAmount = transaction.getAmount() + fee;

        // Update account balances
        sourceAccount.setBalance(sourceAccount.getBalance() - totalAmount);
        destAccount.setBalance(destAccount.getBalance() + transaction.getAmount());

        // Save updated accounts
        accountService.saveAccount(sourceAccount);
        accountService.saveAccount(destAccount);

        // Update and save transaction
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
    }

    @Override
    public Double calculateTransactionFee(TransactionRequest request) {
        return request.getType() == TransactionType.INSTANT ?
                request.getAmount() * 0.005 : // 0.5%
                request.getAmount() * 0.001;  // 0.1%
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAccountTransactionHistory(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        Account account = accountService.getAccountEntity(accountId);
        return transactionMapper.toResponseList(
                transactionRepository.findAccountTransactions(account, startDate, endDate)
        );
    }

    @Override
    public Transaction getTransactionEntity(Long transactionId) {
        return findTransactionById(transactionId);
    }

    private Transaction findTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
    }
}