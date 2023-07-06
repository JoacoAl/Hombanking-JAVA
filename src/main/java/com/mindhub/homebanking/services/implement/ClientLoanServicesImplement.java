package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.repositories.ClientLoanRepository;
import com.mindhub.homebanking.services.ClientLoanServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientLoanServicesImplement implements ClientLoanServices {
    @Autowired
    ClientLoanRepository clientLoanRepository;
    @Override
    public void save(ClientLoan clientLoan) {
        clientLoanRepository.save(clientLoan);
    }
}
