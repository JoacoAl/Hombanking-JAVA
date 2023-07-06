package com.mindhub.homebanking.dtos;

import java.util.List;

public class LoanApplicationDTO {

    private Long id;
    private double amount;
    private int dues;
    private String destinationAccountNumber;


    public LoanApplicationDTO() {
    }

    public LoanApplicationDTO(Long id, double amount, int dues, String destinationAccountNumber, String loanName) {
        this.id = id;
        this.amount = amount;
        this.dues = dues;
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public int getDues() {
        return dues;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

}
