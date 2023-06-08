package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class HomebankingApplication {


	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
		return (args) -> {
			/*Client*/
			Client melba = new Client("Melba", "Morel", "melba@mindhub.com");
			Client joaco = new Client("Joaquin", "Altamonte", "joaquin.altamonte@gmail.com");
			clientRepository.save(melba);
			clientRepository.save(joaco);
			/*Account*/
			Account account = new Account("VIN001", LocalDate.now(), 5000);
			Account account2 = new Account("VIN002", LocalDate.now().plusDays(1), 7500);
			Account account3 = new Account("VIN003", LocalDate.now().minusDays(14), 100.000);
			melba.addAccount(account);
			accountRepository.save(account);
			melba.addAccount(account2);
			accountRepository.save(account2);
			joaco.addAccount(account3);
			accountRepository.save(account3);
			/*Transaction*/
			Transaction transactionMelba = new Transaction(TransactionType.CREDIT, 2000, "mcdonalds", LocalDateTime.now());
			Transaction transactionMelba2 = new Transaction(TransactionType.DEBIT, -5000, "clothes", LocalDateTime.now());
			Transaction transactionMelba3 = new Transaction(TransactionType.CREDIT, 100, "food", LocalDateTime.now().minusDays(4));
			Transaction transactionMelba4 = new Transaction(TransactionType.DEBIT, -340, "cinema", LocalDateTime.now());
			account.addTransactions(transactionMelba);
			transactionRepository.save(transactionMelba);
			account.addTransactions(transactionMelba2);
			transactionRepository.save(transactionMelba2);
			account2.addTransactions(transactionMelba3);
			transactionRepository.save(transactionMelba3);
			account2.addTransactions(transactionMelba4);
			transactionRepository.save(transactionMelba4);
			Transaction transactionJoaco = new Transaction(TransactionType.CREDIT, 500, "soccer", LocalDateTime.now().plusDays(3));
			account3.addTransactions(transactionJoaco);
			transactionRepository.save(transactionJoaco);
		};
	}
}
