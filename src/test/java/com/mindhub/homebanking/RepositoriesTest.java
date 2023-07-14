package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class RepositoriesTest {


    @Autowired
    LoanRepository loanRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    TransactionRepository transactionRepository;

    //Loan
    @Test
    public void existLoans() {

        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, is(not(empty())));

    }

    @Test
    public void existPersonalLoan() {

        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, hasItem(hasProperty("name", is("Personal"))));

    }

    //Client
    @Test
    public void existClients() {
        List<Client> clients = clientRepository.findAll();
        assertThat(clients, is(not(empty())));
    }

    @Test
    public void everyClientHasALastName() {
        List<Client> clients = clientRepository.findAll();
        assertThat(clients, hasItem(hasProperty("lastName", not(emptyString()))));
    }

    //Account
    @Test
    public void eachAccountNumberHasVIN() {
        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts, hasItem(hasProperty("number", startsWith("VIN"))));
    }

    @Test
    public void everyAccountNeedCreationDate() {
        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts, hasItem(hasProperty("creationDate", not(equalTo(null)))));
    }

    //Card

    @Test
    public void eachCardNumberMustHave19Digits() {
        List<Card> cards = cardRepository.findAll();
        assertThat(cards, hasItem(hasProperty("number", hasLength(19))));
        //19 porque mis numeros tienen espacios
    }

    @Test
    public void checkIfCardHolderIsAString() {
        List<Card> cards = cardRepository.findAll();
        assertThat(cards, hasItem( hasProperty("cardHolder", isA(String.class) )));
    }

    //Transactions

    @Test
    public void checkIfLocalDateTimeIsALocalDateTime(){
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions, hasItem(hasProperty("date", isA(LocalDateTime.class))));
    }


    @Test
    public void checkIfTheAmountIsNot0(){
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions, hasItem(hasProperty("amount", is(not(equalTo(0.0))))));
    }


}