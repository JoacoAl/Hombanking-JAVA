package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TypeAccount;
import com.mindhub.homebanking.services.AccountServices;
import com.mindhub.homebanking.services.ClientServices;
import com.mindhub.homebanking.services.TransactionServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountServices accountServices;
    @Autowired
    private ClientServices clientServices;
    @Autowired
    private TransactionServices transactionServices;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountServices.getAllAccounts();
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccounts(@PathVariable Long id) {
        return accountServices.getAccountDTO(id);
    }


    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(@RequestParam TypeAccount typeAccount, Authentication authentication) {

        Client client = clientServices.findByEmail(authentication.getName());
        Set<Account> authenticatedClientAccounts = client.getAccounts();
        Set<Account> accountsActive = authenticatedClientAccounts.stream().filter(acc -> acc.isActive()).collect(Collectors.toSet());


        String randomNumber;

        do {
            Random randomValue = new Random();
            randomNumber = "VIN" + randomValue.nextInt(99999999);
        } while (accountServices.findByNumber(randomNumber) != null);

        if (typeAccount == null) {
            return new ResponseEntity<>("You have to select the type of account", HttpStatus.FORBIDDEN);
        }

        if (accountsActive.size() == 3) {
            return new ResponseEntity<>("You reached the limit of accounts", HttpStatus.FORBIDDEN);
        } else {
            Account newAccount = new Account(randomNumber, LocalDate.now(), 0, true, typeAccount);
            client.addAccount(newAccount);
            accountServices.save(newAccount);
            return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
        }

    }

    @PutMapping("/clients/current/accounts")
    public ResponseEntity<Object> deleteAccount(String accountNumber, Authentication authentication) {

        if (authentication.getName() == null) {
            return new ResponseEntity<>("The user authentication process failed", HttpStatus.FORBIDDEN);
        }
        if (accountNumber == null) {
            return new ResponseEntity<>("The account number you want to delete is wrong", HttpStatus.FORBIDDEN);
        }
        Client client = clientServices.findByEmail(authentication.getName());
        Set<Account> accounts = client.getAccounts();
        Account account = accounts.stream().filter(acc -> acc.getNumber().equals(accountNumber)).findFirst()
                .orElse(null);
        if (account.getBalance() > 0) {
            return new ResponseEntity<>("The balance has to be 0", HttpStatus.FORBIDDEN);
        }

        Set<Transaction> transactions = account.getTransactions();
        transactions.forEach(tr -> tr.setActive(false));
        transactions.forEach(tr -> transactionServices.save(tr));
        account.setActive(false);
        accountServices.save(account);
        return new ResponseEntity<>("The account was deleted correctly", HttpStatus.OK);
    }

    @GetMapping("/getAccounts")
    public Set<AccountDTO> getAccounts(Authentication authentication) {

        Client client = clientServices.findByEmail(authentication.getName());

        Set<AccountDTO> accountsDTOset = client.getAccounts().stream().map(acc -> new AccountDTO(acc)).collect(Collectors.toSet());

        return accountsDTOset.stream().filter(accountDTO -> accountDTO.isActive()).collect(Collectors.toSet());

    }
}
