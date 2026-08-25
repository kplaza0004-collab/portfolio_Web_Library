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
     * 月別実績集計用の検索クエリ
     */
    @Query("SELECT r FROM Rental r WHERE " +
           "(:userId IS NULL OR :userId = '' OR r.userId = :userId) AND " +
           "(:startDate IS NULL OR r.rentalDate >= :startDate) AND " +
           "(:endDate IS NULL OR r.rentalDate <= :endDate)")
    List<Rental> findRentalsForStats(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 会員別レンタル実績ベスト10を取得 (PostgreSQL完全対応)
     */
    @Query(value = "SELECT LPAD(CAST(r.user_id AS text), 8, '0') AS userName, COUNT(r.id) AS rentalCount " +
                   "FROM rentals r " +
                   "GROUP BY r.user_id " +
                   "ORDER BY COUNT(r.id) DESC " +
                   "LIMIT 10", nativeQuery = true)
    List<Object[]> findTopUserRanking();

    /**
     * 書籍別レンタル実績ベスト10を取得 (PostgreSQL完全対応)
     */
    @Query(value = "SELECT bh.title, COUNT(r.id) AS rentalCount " +
                   "FROM rentals r " +
                   "JOIN book_history bh ON r.book_history_id = bh.id " +
                   "GROUP BY bh.id, bh.title " +
                   "ORDER BY COUNT(r.id) DESC " +
                   "LIMIT 10", nativeQuery = true)
    List<Object[]> findTopBookRanking();
}