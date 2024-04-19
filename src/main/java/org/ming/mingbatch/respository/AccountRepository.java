package org.ming.mingbatch.respository;

import org.ming.mingbatch.dto.response.TransactionCountByAccountDto;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AccountRepository {
    public static final String SELECT_TRANSACTION_COUNT_BY_CUSTOMER_QUERY =
            "SELECT customer_customer_id as customer_id, count(*) FROM ACCOUNT a " +
            "LEFT JOIN customer_account ca on a.account_id = ca.account_account_id " +
            "LEFT JOIN transaction t on ca.account_account_id = t.account_account_id " +
            "GROUP BY customer_customer_id";
    private final DataSource dataSource;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public AccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<TransactionCountByAccountDto> findTransactionCountByAccount() {
        return jdbcTemplate.query(SELECT_TRANSACTION_COUNT_BY_CUSTOMER_QUERY, (rs, rowNum) -> {
            TransactionCountByAccountDto response = new TransactionCountByAccountDto();
            response.setCustomerId(rs.getInt("customer_id"));
            response.setCount(rs.getInt("count"));
            return response;
        });
    }
}
