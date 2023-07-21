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
import java.time.format.DateTimeFormatter;
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

            Client melba = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("melba123"));
            Client joaco = new Client("Joaquin", "Altamonte", "joaquin.altamonte@gmail.com", passwordEncoder.encode("joaco123"));
            Client admin = new Client("admin", "admin", "admin@admin.com", passwordEncoder.encode("admin"));
            clientRepository.save(melba);
            clientRepository.save(joaco);
            clientRepository.save(admin);


            Account account = new Account("VIN001", LocalDate.now(), 5000, true, TypeAccount.SAVING);
            Account account2 = new Account("VIN002", LocalDate.now().plusDays(1), 7500,true, TypeAccount.CURRENT);
            Account account3 = new Account("VIN003", LocalDate.now().minusDays(14), 100000,true, TypeAccount.CURRENT);
            Account accountAdmin = new Account("VIN004", LocalDate.now().minusDays(14), 0,true, TypeAccount.CURRENT);
            melba.addAccount(account);
            accountRepository.save(account);
            melba.addAccount(account2);
            accountRepository.save(account2);
            joaco.addAccount(account3);
            accountRepository.save(account3);
            admin.addAccount(accountAdmin);
            accountRepository.save(accountAdmin);

            Transaction transactionMelba = new Transaction(TransactionType.CREDIT, 2000, "mcdonalds", LocalDate.now(), true,2000);
            Transaction transactionMelba2 = new Transaction(TransactionType.DEBIT, -5000, "clothes", LocalDate.now().plusDays(2), true, -5000);
            Transaction transactionMelba3 = new Transaction(TransactionType.CREDIT, 100, "food", LocalDate.now().minusDays(4), true, 100);
            Transaction transactionMelba4 = new Transaction(TransactionType.DEBIT, -340, "cinema", LocalDate.now(), true,-340);
            Transaction transactionMelba5 = new Transaction(TransactionType.CREDIT, 10000, "UCL FINAL", LocalDate.now().plusDays(5), true,10000);

            Transaction transactionJoaco = new Transaction(TransactionType.CREDIT, 500, "soccer", LocalDate.now().plusMonths(4), true, 500);
            Transaction transactionJoaco2 = new Transaction(TransactionType.DEBIT, -500, "comida", LocalDate.now().minusDays(20), true, -500);
            Transaction transactionJoaco3 = new Transaction(TransactionType.CREDIT, 500, "animales", LocalDate.now(), true, 500);
            Transaction transactionJoaco4 = new Transaction(TransactionType.DEBIT, -1000, "cine", LocalDate.now().plusDays(2), true, -1000);

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
            account3.addTransactions(transactionJoaco2);
            account3.addTransactions(transactionJoaco3);
            account3.addTransactions(transactionJoaco4);

            transactionRepository.save(transactionJoaco);
            transactionRepository.save(transactionJoaco2);
            transactionRepository.save(transactionJoaco3);
            transactionRepository.save(transactionJoaco4);



            List<Integer> dueMortgage = List.of(12, 24, 36, 48, 60);
            List<Integer> duePersonal = List.of(6, 12, 24);
            List<Integer> dueAutomotive = List.of(6, 12, 24, 36);

            Loan loan1 = new Loan("Mortgage", 500000, dueMortgage, 20);
            Loan loan2 = new Loan("Personal", 100000, duePersonal, 15);
            Loan loan3 = new Loan("Automotive", 300000, dueAutomotive, 40);
            loanRepository.save(loan1);
            loanRepository.save(loan2);
            loanRepository.save(loan3);


            ClientLoan clientloan1 = new ClientLoan(400000, 60, 60, 400000, 20, true);
            ClientLoan clientloan2 = new ClientLoan(50000, 12, 12, 50000, 15, true);
            ClientLoan clientloan3 = new ClientLoan(100000, 24,24, 100000, 15, true);
            ClientLoan clientloan4 = new ClientLoan(200000, 36,36,200000, 40, true);
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



            Card cardMelba = new Card(melba.getFirstName() + " " + melba.getLastName(), CardType.DEBIT, CardColor.GOLD, "4568 9564 7893 0239", 101, LocalDate.now(), LocalDate.now().plusYears(5), true);
            Card cardMelba2 = new Card(melba.getFirstName() + " " + melba.getLastName(), CardType.CREDIT, CardColor.TITANIUM, "3457 2890 1923 8490", 202, LocalDate.now(), LocalDate.now().plusYears(5), true);
            Card cardJoaco = new Card(joaco.getFirstName() + " " + joaco.getLastName(), CardType.DEBIT, CardColor.SILVER, "4689 3456 8902 9345", 110, LocalDate.now(), LocalDate.now().plusDays(10), true);
            Card cardJoaco2 = new Card(joaco.getFirstName() + " " + joaco.getLastName(), CardType.CREDIT, CardColor.TITANIUM, "1234 5678 9087 6543", 211, LocalDate.now(), LocalDate.now().minusDays(10), true);

            melba.addCard(cardMelba);
            melba.addCard(cardMelba2);
            joaco.addCard(cardJoaco);
            joaco.addCard(cardJoaco2);

            cardRepository.save(cardMelba);
            cardRepository.save(cardMelba2);
            cardRepository.save(cardJoaco);
            cardRepository.save(cardJoaco2);


        };
    }
}
