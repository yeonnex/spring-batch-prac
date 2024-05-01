package org.ming.mingbatch.dao;

import org.ming.mingbatch.domain.Transaction;

import java.util.List;

public interface TransactionDao {
    List<Transaction> getTransactionByAccountNumber(String accountNumber);
}
