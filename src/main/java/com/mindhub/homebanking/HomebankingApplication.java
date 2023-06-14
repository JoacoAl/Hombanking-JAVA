package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class HomebankingApplication {


    public static void main(String[] args) {
        SpringApplication.run(HomebankingApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository,
                                      TransactionRepository transactionRepository, LoanRepository loanRepository,
                                      ClientLoanRepository clientLoanRepository) {
        return (args) -> {
            /*Client*/
            Client melba = new Client("Melba", "Morel", "melba@mindhub.com");
            Client joaco = new Client("Joaquin", "Altamonte", "joaquin.altamonte@gmail.com");
            clientRepository.save(melba); clientRepository.save(joaco);
            /*Account*/
            Account account = new Account("VIN001", LocalDate.now(), 5000);
            Account account2 = new Account("VIN002", LocalDate.now().plusDays(1), 7500);
            Account account3 = new Account("VIN003", LocalDate.now().minusDays(14), 100.000); melba.addAccount(account);
            accountRepository.save(account); melba.addAccount(account2); accountRepository.save(account2);
            joaco.addAccount(account3); accountRepository.save(account3);
            /*Transaction*/
            Transaction transactionMelba = new Transaction(TransactionType.CREDIT, 2000, "mcdonalds",
                    LocalDateTime.now());
            Transaction transactionMelba2 = new Transaction(TransactionType.DEBIT, -5000, "clothes",
                    LocalDateTime.now().plusDays(2));
            Transaction transactionMelba3 = new Transaction(TransactionType.CREDIT, 100, "food",
                    LocalDateTime.now().minusDays(4));
            Transaction transactionMelba4 = new Transaction(TransactionType.DEBIT, -340, "cinema", LocalDateTime.now());
            Transaction transactionMelba5 = new Transaction(TransactionType.CREDIT, 10000, "UCL FINAL",
                    LocalDateTime.now().plusDays(5));
            account.addTransactions(transactionMelba); transactionRepository.save(transactionMelba);
            account.addTransactions(transactionMelba2); transactionRepository.save(transactionMelba2);
            account.addTransactions(transactionMelba5); transactionRepository.save(transactionMelba5);
            account2.addTransactions(transactionMelba3); transactionRepository.save(transactionMelba3);
            account2.addTransactions(transactionMelba4); transactionRepository.save(transactionMelba4);
            Transaction transactionJoaco = new Transaction(TransactionType.CREDIT, 500, "soccer", LocalDateTime.now());
            account3.addTransactions(transactionJoaco); transactionRepository.save(transactionJoaco);
            /*Loan*/
            Set<Integer> dueMortgage = Set.of(12, 24, 36, 48, 60);
            Set<Integer> duePersonal = Set.of(6, 12, 24);
            Set<Integer> dueAutomotive = Set.of(6, 12, 24, 36);

            Loan loan1 = new Loan("Mortgage", 500000, dueMortgage);
            Loan loan2 = new Loan("Personal", 100000, duePersonal);
            Loan loan3 = new Loan("Automotive", 300000, dueAutomotive);
            loanRepository.save(loan1);
            loanRepository.save(loan2);
            loanRepository.save(loan3);

            /*ClientLoan*/
            ClientLoan clientloan1 = new ClientLoan(400000, 60, melba, loan1);
            clientLoanRepository.save(clientloan1);
            ClientLoan clientloan2 = new ClientLoan(50000, 12, melba, loan2);
            clientLoanRepository.save(clientloan2);
            ClientLoan clientloan3 = new ClientLoan(100000, 24, joaco, loan2);
            clientLoanRepository.save(clientloan3);
            ClientLoan clientloan4 = new ClientLoan(200000, 36, joaco, loan3);
            clientLoanRepository.save(clientloan4);
        };
    }
}
