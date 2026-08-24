package com.example.userTest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userTest.entity.BookHistory;
import com.example.userTest.entity.Favorite;
import com.example.userTest.repository.BookHistoryRepository;
import com.example.userTest.repository.FavoriteRepository;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private BookHistoryRepository bookHistoryRepository;

    /**
     * ユーザーIDに紐づくお気に入り一覧を取得（FavoriteController 用）
     */
    @Transactional(readOnly = true)
    public List<Favorite> getFavoritesByUserId(String userId) {
        return favoriteRepository.findByUserId(userId);
    }

    /**
     * お気に入り追加（String, String 型を受け取るオーバーロード）
     * TopController / FavoriteController の addFavorite(userId, bookId) に対応
     */
    @Transactional
    public void addFavorite(String userId, String googleBookId) {
        // googleBookId から BookHistory を検索（無ければ作成して保存）
        BookHistory bookHistory = bookHistoryRepository.findByGoogleBookId(googleBookId)
                .orElseGet(() -> {
                    BookHistory newBook = new BookHistory();
                    newBook.setGoogleBookId(googleBookId);
                    return bookHistoryRepository.save(newBook);
                });

        addFavorite(userId, bookHistory);
    }

    /**
     * お気に入り追加（String, BookHistory 型を受け取るメソッド）
     */
    @Transactional
    public void addFavorite(String userId, BookHistory bookHistory) {
        // 親テーブル（book_history）のデータを確実に保存
        BookHistory savedBook = bookHistoryRepository.save(bookHistory);

        // Favorite エンティティを作成して保存
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookHistory(savedBook);

        favoriteRepository.save(favorite);
    }

    /**
     * お気に入り削除（TopController / FavoriteController 用）
     */
    @Transactional
    public void removeFavorite(String userId, String googleBookId) {
        bookHistoryRepository.findByGoogleBookId(googleBookId).ifPresent(bookHistory -> {
            favoriteRepository.deleteByUserIdAndBookHistory(userId, bookHistory);
        });
    }
}