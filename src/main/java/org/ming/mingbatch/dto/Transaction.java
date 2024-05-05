package org.ming.mingbatch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    private String accountNumber;
    private String transactionDate;
    private double amount;
}
