package org.ming.mingbatch.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountSummary {
    private int id;
    private String accountNumber;
    private double currentBalance;
}
