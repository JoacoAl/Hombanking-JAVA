package com.mindhub.homebanking.services;

import com.mindhub.homebanking.controllers.TransactionsController;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface TransactionServices {
    void save (Transaction transaction);
    Set<Transaction> getTransactionsByAccountAndDateRange(Account account, LocalDate startDate, LocalDate endDate);

}
