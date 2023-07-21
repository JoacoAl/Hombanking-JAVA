package com.mindhub.homebanking.dtos;

import javax.persistence.Entity;


public class LoanPayDTO {

    private long id;
    private String numberAccount;
    private String description;

    private int payment;

    public LoanPayDTO() {
    }

    public LoanPayDTO(long id, String numberAccount, String description, int payment) {
        this.id = id;
        this.numberAccount = numberAccount;
        this.description = description;
        this.payment = payment;
    }

    public long getId() {
        return id;
    }

    public String getNumberAccount() {
        return numberAccount;
    }

    public String getDescription() {
        return description;
    }

    public int getPayment() {
        return payment;
    }
}
