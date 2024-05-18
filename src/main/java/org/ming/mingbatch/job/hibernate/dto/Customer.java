package org.ming.mingbatch.job.hibernate.dto;


import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@ToString
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    private Long id;

    private String firstName;

    private String middleInitial;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zipCode;

}
