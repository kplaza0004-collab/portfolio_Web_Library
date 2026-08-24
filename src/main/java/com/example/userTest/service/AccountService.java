package com.example.userTest.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.userTest.entity.Account;
import com.example.userTest.entity.Role;
import com.example.userTest.form.AccountForm;
import com.example.userTest.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existsById(String id) {
        return accountRepository.existsById(id);
    }

    public void registerAccount(AccountForm form, Role role) {
        String encodedPassword = passwordEncoder.encode(form.getPassword());
        Account account = new Account(form.getId(), encodedPassword, role);
        accountRepository.save(account);
    }
}
