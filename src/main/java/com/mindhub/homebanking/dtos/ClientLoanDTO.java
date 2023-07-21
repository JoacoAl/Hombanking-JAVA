package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class ClientLoanDTO {

    private Long id;
    private Long loanId;
    private String name;
    private double amount;
    private int payments;

    private double remainingAmount;
    private int remainingPayments;

    private int percentage;

    private boolean active;


    public ClientLoanDTO(ClientLoan clientLoan) {

        this.id = clientLoan.getId();
        this.loanId = clientLoan.getLoan().getId();
        this.name = clientLoan.getLoan().getName();
        this.amount = clientLoan.getAmount();
        this.payments = clientLoan.getPayments();
        this.remainingPayments = clientLoan.getRemainingPayments();
        this.remainingAmount = clientLoan.getRemainingAmount();
        this.percentage = clientLoan.getPercentage();
        this.active = clientLoan.isActive();
    }
    public Long getId() {
        return id;
    }

    public Long getLoanId() {
        return loanId;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public int getPayments() {
        return payments;
    }

    public int getRemainingPayments() {
        return remainingPayments;
    }
    public double getRemainingAmount() {
        return remainingAmount;
    }

    public int getPercentage() {
        return percentage;
    }

    public boolean isActive() {
        return active;
    }
}




