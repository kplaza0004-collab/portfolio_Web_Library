package com.example.userTest.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.userTest.entity.Account;
import com.example.userTest.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DBから会員情報を取得（IDで検索）
        Account account = accountRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Spring Security ユーザーオブジェクトの構築
        return org.springframework.security.core.userdetails.User
                .withUsername(account.getUserId()) 
                .password(account.getPassword())
                .roles(account.getRole().name()) 
                .build();
    }
}