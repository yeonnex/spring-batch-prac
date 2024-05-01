package org.ming.mingbatch.dao;

import org.ming.mingbatch.domain.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class TransactionDaoSupport extends JdbcTemplate implements TransactionDao {

    public TransactionDaoSupport(DataSource dataSource) {
        super(dataSource);
    }
    @Override
    public List<Transaction> getTransactionByAccountNumber(String accountNumber) {
        return query(
                "SELECT t.id, t.timestamp, t.amount FROM transaction t " +
                        "INNER JOIN account_summary am ON am.id = t.account_summary_id " +
                        "WHERE am.account_number = ? ",
                new Object[] {accountNumber},
                (rs, rowNum) -> {
                    Transaction transaction = new Transaction();
                    transaction.setAmount(rs.getDouble("amount"));
//                    transaction.setTimestamp(rs.getDate("timestamp"));
                    return transaction;
                }
        );
    }
}
