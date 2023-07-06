package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AccountServices {

    Account findByNumber (String number);

    AccountDTO getAccountDTO (Long id);
    List<AccountDTO> getAllAccounts ();
    void save (Account account);

}
