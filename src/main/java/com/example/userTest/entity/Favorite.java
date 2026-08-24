package com.example.userTest.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "favorites")
@Data
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    // ★ referencedColumnName を BookHistory 側の Javaフィールド名 "googleBookId" に修正！
    @ManyToOne
    @JoinColumn(name = "google_book_id", referencedColumnName = "googleBookId")
    private BookHistory bookHistory;

    private LocalDateTime createdAt = LocalDateTime.now();

    public String getGoogleBookId() {
        return bookHistory != null ? bookHistory.getGoogleBookId() : null;
    }
}