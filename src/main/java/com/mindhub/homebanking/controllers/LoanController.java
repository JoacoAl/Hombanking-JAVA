package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.ClientLoanDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.*;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    ClientServices clientServices;

    @Autowired
    ClientLoanServices clientLoanServices;

    @Autowired
    AccountServices accountServices;

    @Autowired
    TransactionServices transactionServices;
    @Autowired
    LoanServices loanServices;

@GetMapping("/loans")
public List<LoanDTO> getLoans (){
return loanServices.getLoansDTO();
}
    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createClientLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication) {

        Client client = clientServices.findByEmail(authentication.getName());
        Loan loan = loanServices.findById(loanApplicationDTO.getId());
        Account destinationAccount = accountServices.findByNumber(loanApplicationDTO.getDestinationAccountNumber());


        if (loanApplicationDTO.getAmount() == 0 || loanApplicationDTO.getDues() == 0 || loanApplicationDTO.getDestinationAccountNumber().isBlank()) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }


        if (loan == null) {
            return new ResponseEntity<>("The loan does not exist", HttpStatus.FORBIDDEN);
        }

        if (loan.getId() == 0){

return new ResponseEntity<>("Loan type does not exist",HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("The amount requested is greater than the maximum amount", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getAmount() <= 0) {
            return new ResponseEntity<>("The amount must be greater than 0", HttpStatus.FORBIDDEN);
        }

        if (!loan.getPayments().stream().anyMatch(payment -> payment.equals(loanApplicationDTO.getDues()))) {
            return new ResponseEntity<>("The amount of installments requested are not available for this loan", HttpStatus.FORBIDDEN);
        }

        if (accountServices.findByNumber(loanApplicationDTO.getDestinationAccountNumber()) == null) {
            return new ResponseEntity<>("The account does not exist", HttpStatus.FORBIDDEN);
        }

        if (!client.getAccounts().stream().anyMatch(account -> account.getNumber().equals(loanApplicationDTO.getDestinationAccountNumber()))) {
            return new ResponseEntity<>("The destination account does not belong to the client", HttpStatus.FORBIDDEN);
        } else {
            ClientLoan clientLoan = new ClientLoan(loanApplicationDTO.getAmount(), loanApplicationDTO.getDues());
            Transaction loanTransaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName() + " " + "loan approved", LocalDateTime.now());

            client.addClientLoan(clientLoan);
            loan.addClientLoan(clientLoan);
            destinationAccount.addTransactions(loanTransaction);

            clientLoanServices.save(clientLoan);
            transactionServices.save(loanTransaction);
            loanServices.save(loan);

            destinationAccount.setBalance(destinationAccount.getBalance() + loanApplicationDTO.getAmount());
            accountServices.save(destinationAccount);


            return new ResponseEntity<>("The requested loan was successfully approved", HttpStatus.CREATED);


        }


    }
}
