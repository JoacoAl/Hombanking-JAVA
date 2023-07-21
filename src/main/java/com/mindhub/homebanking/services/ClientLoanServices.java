package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;
import org.springframework.stereotype.Service;

public interface ClientLoanServices {
    void save (ClientLoan clientLoan);

    ClientLoan findById (long id);
}
