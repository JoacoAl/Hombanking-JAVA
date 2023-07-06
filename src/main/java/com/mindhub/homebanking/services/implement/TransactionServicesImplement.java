package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.TransactionServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TransactionServicesImplement implements TransactionServices {
@Autowired
    TransactionRepository transactionRepository;
    @Override
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
