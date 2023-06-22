package com.mindhub.homebanking.models;

import com.mindhub.homebanking.dtos.AccountDTO;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

    @Entity
    public class Client {
        @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String firstName;
    private String lastName;
    private String email;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<ClientLoan> clientLoans = new HashSet<>();

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<Card> cards = new HashSet<>();

    public Client() {}

    public Client(String first, String last, String contact
    ) {
        firstName = first;
        lastName = last;
        email = contact;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }


    public void addAccount(Account account) {
        account.setClient(this);
        accounts.add(account);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return firstName + " " + lastName + " " + email;
    }

    public long getId() {
        return id;
    }

    @JsonIgnore
    public  Set<ClientLoan> getLoans() {
        return clientLoans;
    }

    public  Set<ClientLoan> getClientLoans() {
        return clientLoans;
    }

    public void setClientLoans( Set<ClientLoan> clientLoans) {
        this.clientLoans = clientLoans;
    }

    public void addClientLoan(ClientLoan clientLoan){
        clientLoan.setClient(this);
        clientLoans.add(clientLoan);
    }

    public Set<Card> getCards() {
        return cards;
    }

    public void addCard(Card card){
        card.setClient(this);
        cards.add(card);
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }
}

