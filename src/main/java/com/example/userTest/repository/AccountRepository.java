package com.example.userTest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.userTest.entity.Account;
import com.example.userTest.entity.Role;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    // ロール指定（USER または ADMIN）でアカウント一覧を取得
    List<Account> findByRole(Role role);
}