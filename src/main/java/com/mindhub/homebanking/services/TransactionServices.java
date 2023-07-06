package com.mindhub.homebanking.services;

import com.mindhub.homebanking.controllers.TransactionsController;
import com.mindhub.homebanking.models.Transaction;
import org.springframework.stereotype.Service;

public interface TransactionServices {
    void save (Transaction transaction);
}
