package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;
import org.springframework.stereotype.Service;

import java.util.List;

public interface LoanServices {

    List<LoanDTO> getLoansDTO ();

    Loan findById (Long id);

    void save (Loan loan);
}
