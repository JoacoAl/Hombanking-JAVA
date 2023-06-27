package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomebankingApplication.class, args);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository,
                                      TransactionRepository transactionRepository, LoanRepository loanRepository,
                                      ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
        return (args) -> {
            /*Client*/
            Client melba = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("melba123"));
            Client joaco = new Client("Joaquin", "Altamonte", "joaquin.altamonte@gmail.com", passwordEncoder.encode("joaco123"));
            Client admin = new Client("admin", "admin", "admin@admin.com", passwordEncoder.encode("admin"));
            clientRepository.save(melba);
            clientRepository.save(joaco);
            clientRepository.save(admin);
            /*Account*/
            Account account = new Account("VIN001", LocalDate.now(), 5000);
            Account account2 = new Account("VIN002", LocalDate.now().plusDays(1), 7500);
            Account account3 = new Account("VIN003", LocalDate.now().minusDays(14), 100.000);
            Account accountAdmin = new Account("VIN004", LocalDate.now().minusDays(14), 0);
            melba.addAccount(account);
            accountRepository.save(account);
            melba.addAccount(account2);
            accountRepository.save(account2);
            joaco.addAccount(account3);
            accountRepository.save(account3);
            admin.addAccount(accountAdmin);
            accountRepository.save(accountAdmin);
            /*Transaction*/
            Transaction transactionMelba = new Transaction(TransactionType.CREDIT, 2000, "mcdonalds", LocalDateTime.now());
            Transaction transactionMelba2 = new Transaction(TransactionType.DEBIT, -5000, "clothes", LocalDateTime.now().plusDays(2));
            Transaction transactionMelba3 = new Transaction(TransactionType.CREDIT, 100, "food", LocalDateTime.now().minusDays(4));
            Transaction transactionMelba4 = new Transaction(TransactionType.DEBIT, -340, "cinema", LocalDateTime.now());
            Transaction transactionMelba5 = new Transaction(TransactionType.CREDIT, 10000, "UCL FINAL", LocalDateTime.now().plusDays(5));
            Transaction transactionJoaco = new Transaction(TransactionType.CREDIT, 500, "soccer", LocalDateTime.now());
            account.addTransactions(transactionMelba);
            transactionRepository.save(transactionMelba);
            account.addTransactions(transactionMelba2);
            transactionRepository.save(transactionMelba2);
            account.addTransactions(transactionMelba5);
            transactionRepository.save(transactionMelba5);
            account2.addTransactions(transactionMelba3);
            transactionRepository.save(transactionMelba3);
            account2.addTransactions(transactionMelba4);
            transactionRepository.save(transactionMelba4);
            account3.addTransactions(transactionJoaco);
            transactionRepository.save(transactionJoaco);
            /*Loan*/
            List<Integer> dueMortgage = List.of(12, 24, 36, 48, 60);
            List<Integer> duePersonal = List.of(6, 12, 24);
            List<Integer> dueAutomotive = List.of(6, 12, 24, 36);

            Loan loan1 = new Loan("Mortgage", 500000, dueMortgage);
            Loan loan2 = new Loan("Personal", 100000, duePersonal);
            Loan loan3 = new Loan("Automotive", 300000, dueAutomotive);
            loanRepository.save(loan1);
            loanRepository.save(loan2);
            loanRepository.save(loan3);

            /*ClientLoan*/
            ClientLoan clientloan1 = new ClientLoan(400000, 60);
            ClientLoan clientloan2 = new ClientLoan(50000, 12);
            ClientLoan clientloan3 = new ClientLoan(100000, 24);
            ClientLoan clientloan4 = new ClientLoan(200000, 36);
            melba.addClientLoan(clientloan1);
            loan1.addClientLoan(clientloan1);
            melba.addClientLoan(clientloan2);
            loan2.addClientLoan(clientloan2);
            joaco.addClientLoan(clientloan3);
            loan2.addClientLoan(clientloan3);
            joaco.addClientLoan(clientloan4);
            loan3.addClientLoan(clientloan4);
            clientLoanRepository.save(clientloan1);
            clientLoanRepository.save(clientloan2);
            clientLoanRepository.save(clientloan3);
            clientLoanRepository.save(clientloan4);

            /*Card*/
            Card cardMelba = new Card(melba.getFirstName() + " " + melba.getLastName(), CardType.DEBIT, CardColor.GOLD, "4568 9564 7893 0239", 101, LocalDate.now(), LocalDate.now().plusYears(5));
            Card cardMelba2 = new Card(melba.getFirstName() + " " + melba.getLastName(), CardType.CREDIT, CardColor.TITANIUM, "3457 2890 1923 8490", 202, LocalDate.now(), LocalDate.now().plusYears(5));
            Card cardJoaco = new Card(melba.getFirstName() + " " + melba.getLastName(), CardType.CREDIT, CardColor.SILVER, "4689 3456 8902 9345", 110, LocalDate.now(), LocalDate.now().plusYears(5));

            melba.addCard(cardMelba);
            melba.addCard(cardMelba2);
            joaco.addCard(cardJoaco);

            cardRepository.save(cardMelba);
            cardRepository.save(cardMelba2);
            cardRepository.save(cardJoaco);
        };
    }
}
