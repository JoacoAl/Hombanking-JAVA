package com.mindhub.homebanking.dtos;

public class TransferDTO {

    private double amount;

    private String description;

    private String numberAccount;

    private String destinationAccount;

    public TransferDTO (){};

    public TransferDTO(double amount, String description, String numberAccount, String destinationAccount) {
        this.amount = amount;
        this.description = description;
        this.numberAccount = numberAccount;
        this.destinationAccount = destinationAccount;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getNumberAccount() {
        return numberAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }
}
