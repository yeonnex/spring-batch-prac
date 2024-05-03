package org.ming.mingbatch.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
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

    private List<Transaction> transactions = new ArrayList<>();

    public static Customer from(List<String> list) {
        Customer customer = new Customer();
        customer.setFirstName(list.get(1));
        customer.setMiddleInitial(list.get(2));
        customer.setLastName(list.get(3));
        customer.setAddress(list.get(4));
        customer.setCity(list.get(5));
        customer.setState(list.get(6));
        return customer;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s has %d transactions",
                this.firstName, this.middleInitial, this.lastName, this.transactions.size());
    }
}
