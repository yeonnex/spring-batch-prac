package org.ming.mingbatch.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.http.client.utils.DateUtils;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Transaction {

    private String accountNumber;
    private Date transactionDate;


    private double amount;

    public static Transaction from(List<String> list) {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(list.get(1));
        String[] patterns = {"yyyy-MM-dd HH:mm:ss"};
        transaction.setTransactionDate(DateUtils.parseDate(list.get(2), patterns));
        transaction.setAmount(Double.parseDouble(list.get(3)));
        return transaction;
    }
}
