package org.ming.mingbatch.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Customer {

    private String firstName;
    private String middleInitial;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zipCode;

    private List<Transaction> transactions;

    @Override
    public String toString() {
        String transactionMsg = transactions.isEmpty() ? "No transactions available" :
                "Transactions count: " + transactions.size();

        return """
                Customer{
                firstName='%s',
                middleInitial='%s',
                lastName='%s',
                address='%s',
                city='%s',
                state='%s',
                zipCode='%s',
                %s
                }
                """.formatted(
                firstName,
                middleInitial,
                lastName,
                address,
                city,
                state,
                zipCode,
                transactionMsg
        );
    }
}
