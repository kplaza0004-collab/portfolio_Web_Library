package com.example.userTest.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.userTest.entity.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // ユーザーIDとステータスでレンタル一覧を取得
    List<Rental> findByUserIdAndStatus(String userId, String status);

    // ユーザーの現在のレンタル件数をカウント
    long countByUserIdAndStatus(String userId, String status);

    /**
     * 月別実績集計用の検索クエリ (標準JPQL)
     */
    @Query("SELECT r FROM Rental r WHERE " +
           "(:userId IS NULL OR r.userId = :userId) AND " +
           "(:startDate IS NULL OR r.rentalDate >= :startDate) AND " +
           "(:endDate IS NULL OR r.rentalDate <= :endDate)")
    List<Rental> findRentalsForStats(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 会員別レンタル実績ベスト10を取得 (標準JPQL)
     */
    @Query("SELECT r.userId, COUNT(r) FROM Rental r " +
           "GROUP BY r.userId " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> findTopUserRanking();

    /**
     * 書籍別レンタル実績ベスト10を取得 (標準JPQL)
     */
    @Query("SELECT r.bookHistory.title, COUNT(r) FROM Rental r " +
           "GROUP BY r.bookHistory.title " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> findTopBookRanking();
}