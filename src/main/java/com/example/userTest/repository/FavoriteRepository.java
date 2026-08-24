package com.example.userTest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.userTest.entity.BookHistory;
import com.example.userTest.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 1. お気に入り一覧を取得するメソッド
    List<Favorite> findByUserId(String userId);

    // 2. お気に入りを削除するメソッド
    void deleteByUserIdAndBookHistory(String userId, BookHistory bookHistory);
}