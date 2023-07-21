package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.*;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.*;
import org.apache.catalina.LifecycleState;
import org.apache.coyote.Response;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
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
    public List<LoanDTO> getLoans() {
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

        if (loan.getId() == 0) {

            return new ResponseEntity<>("Loan type does not exist", HttpStatus.FORBIDDEN);
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
            ClientLoan clientLoan = new ClientLoan(loanApplicationDTO.getAmount(), loanApplicationDTO.getDues(), loanApplicationDTO.getDues(), loanApplicationDTO.getAmount() * (1 + loan.getPercentage() / 100.0), loan.getPercentage(), true);
            Transaction loanTransaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName() + " " + "loan approved", LocalDate.now(), true, destinationAccount.getBalance() + loanApplicationDTO.getAmount());

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

    @Transactional
    @PostMapping("/loans/payments")
    public ResponseEntity<Object> payLoanPayments(Authentication authentication, @RequestBody LoanPayDTO loanPayDTO) {

        Client client = clientServices.findByEmail(authentication.getName());
        ClientLoan clientLoan = clientLoanServices.findById(loanPayDTO.getId());
        Account account = accountServices.findByNumber(loanPayDTO.getNumberAccount());

        if (client == null) {
            return new ResponseEntity<>("Who are you?", HttpStatus.FORBIDDEN);
        }

        if (loanPayDTO.getNumberAccount().isBlank() || loanPayDTO.getId() == 0 || loanPayDTO.getDescription().isBlank()) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }

        if (clientLoan == null) {
            return new ResponseEntity<>("The Client loan does not exist", HttpStatus.FORBIDDEN);
        }

        double feeToPay = clientLoan.getAmount() * (1 + clientLoan.getLoan().getPercentage() / 100.0) / clientLoan.getPayments();

        double totalFee = feeToPay * loanPayDTO.getPayment();

        if (account.getBalance() < totalFee) {
            return new ResponseEntity<>("Insuficient founds", HttpStatus.FORBIDDEN);
        }

        if (totalFee <= 0) {
            //clientLoan.setActive(false);
            //clientLoanServices.save(clientLoan);
            return new ResponseEntity<>("The loan was paid", HttpStatus.FORBIDDEN);
        }


        clientLoan.setRemainingPayments((int) (clientLoan.getRemainingPayments() - loanPayDTO.getPayment()));
        clientLoan.setRemainingAmount(clientLoan.getRemainingAmount() - totalFee);
        clientLoanServices.save(clientLoan);

        account.setBalance(account.getBalance() - totalFee);

        Transaction transaction = new Transaction(TransactionType.DEBIT, -totalFee, loanPayDTO.getDescription() + " " + clientLoan.getLoan().getName(), LocalDate.now(), true, account.getBalance());

        transactionServices.save(transaction);

        account.addTransactions(transaction);
        accountServices.save(account);

        return new ResponseEntity<>("The payment was paid successfully", HttpStatus.OK);
    }


    @PostMapping("/createLoans")
    public ResponseEntity<Object> createLoans(@RequestBody LoanDTO loanDTO, Authentication authentication) {

        Client admin = clientServices.findByEmail(authentication.getName());
        if (admin == null) {
            return new ResponseEntity<>("Who are you?", HttpStatus.FORBIDDEN);
        }

        if (loanDTO.getName().isBlank() || loanDTO.getMaxAmount() == 0 || loanDTO.getPayments().isEmpty() || loanDTO.getPercentage() == 0) {
            return new ResponseEntity<>("Complete the fields well", HttpStatus.FORBIDDEN);
        }

        Loan newLoan = new Loan(loanDTO.getName(), loanDTO.getMaxAmount(), loanDTO.getPayments(), loanDTO.getPercentage());
        loanServices.save(newLoan);


        return new ResponseEntity<>("The loan was created", HttpStatus.CREATED);

    }

}
