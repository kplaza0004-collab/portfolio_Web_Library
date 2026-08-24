package com.example.userTest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.userTest.entity.BookHistory;

// 第二引数（主キー型）を String から Long に変更
public interface BookHistoryRepository extends JpaRepository<BookHistory, Long> {

    // Google Book ID (String) で検索するためのメソッドを追加
    Optional<BookHistory> findByGoogleBookId(String googleBookId);
}