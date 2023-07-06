package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AccountServicesImplement implements AccountServices {

    @Autowired
    AccountRepository accountRepository;
    @Override
    public Account findByNumber(String number) {
        return accountRepository.findByNumber(number);
    }

    @Override
    public AccountDTO getAccountDTO(Long id) {
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);

    }

    @Override
    public List<AccountDTO> getAllAccounts() {
        return  accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toList());

    }

    @Override
    public void save(Account account) {
        accountRepository.save(account);
    }
}
