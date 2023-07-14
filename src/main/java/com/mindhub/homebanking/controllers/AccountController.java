package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountServices;
import com.mindhub.homebanking.services.ClientServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    AccountServices accountServices;
    @Autowired
    ClientServices clientServices;
    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountServices.getAllAccounts();
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccounts(@PathVariable Long id) {
        return accountServices.getAccountDTO(id);
    }


    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication) {

        Client client = clientServices.findByEmail(authentication.getName());
        Set<Account> authenticatedClientAccounts = client.getAccounts();

        String randomNumber;

        do {
            Random randomValue = new Random();
            randomNumber = "VIN" + randomValue.nextInt(99999999);
        } while (accountServices.findByNumber(randomNumber) != null);


        if (authenticatedClientAccounts.size() == 3) {
            return new ResponseEntity<>("You reached the limit of accounts", HttpStatus.FORBIDDEN);
        } else {
            Account newAccount = new Account(randomNumber, LocalDate.now(), 0);
            client.addAccount(newAccount);
            accountServices.save(newAccount);
            return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
        }
    }
}
