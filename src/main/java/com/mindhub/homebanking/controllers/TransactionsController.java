package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.TransferDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class TransactionsController {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ClientRepository clientRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> makeTransfer(Authentication authentication, @RequestBody TransferDTO transferDTO) {
        Client client = clientRepository.findByEmail(authentication.getName());
        Set<Account> clientAccounts = client.getAccounts();

        if (transferDTO.getAmount() == 0 || transferDTO.getDescription().isBlank() || transferDTO.getNumberAccount().isBlank() || transferDTO.getDestinationAccount().isBlank()) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }

        if (accountRepository.findByNumber(transferDTO.getNumberAccount()).equals(transferDTO.getDestinationAccount())) {
            return new ResponseEntity<>("Same accounts",HttpStatus.FORBIDDEN);
        }

        if (accountRepository.findByNumber(transferDTO.getNumberAccount()) == null){
            return new ResponseEntity<>("Origin account doesn't exist",HttpStatus.FORBIDDEN);
        }

        if (!clientAccounts.stream().anyMatch(account -> account.getNumber().equals(transferDTO.getNumberAccount()))){
            return new ResponseEntity<>("This account does not belong to this client",HttpStatus.FORBIDDEN);
        }

        if (accountRepository.findByNumber(transferDTO.getDestinationAccount()) == null){
            return new ResponseEntity<>("Destination account does not exist",HttpStatus.FORBIDDEN);
        }

        if (accountRepository.findByNumber(transferDTO.getNumberAccount()).getBalance() < transferDTO.getAmount()){
            return new ResponseEntity<>("Insuficient founds",HttpStatus.FORBIDDEN);
        }else  {
            Transaction transactionDebit = new Transaction(TransactionType.DEBIT, -transferDTO.getAmount(), transferDTO.getDescription() + " " + transferDTO.getDestinationAccount(), LocalDateTime.now());
            Transaction transactionCredit = new Transaction(TransactionType.CREDIT, transferDTO.getAmount(), transferDTO.getDescription() + " " + transferDTO.getNumberAccount(), LocalDateTime.now());

            accountRepository.findByNumber(transferDTO.getNumberAccount()).addTransactions(transactionDebit);
            accountRepository.findByNumber(transferDTO.getDestinationAccount()).addTransactions(transactionCredit);
            transactionRepository.save(transactionDebit);
            transactionRepository.save(transactionCredit);

            Account accountOrigin = accountRepository.findByNumber(transferDTO.getNumberAccount());
            accountOrigin.setBalance(accountRepository.findByNumber(transferDTO.getNumberAccount()).getBalance() - transferDTO.getAmount());
            accountRepository.save(accountOrigin);
            Account accountDestination =  accountRepository.findByNumber(transferDTO.getDestinationAccount());
            accountDestination.setBalance(accountRepository.findByNumber(transferDTO.getDestinationAccount()).getBalance() + transferDTO.getAmount());
            accountRepository.save(accountDestination);

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }


    }
