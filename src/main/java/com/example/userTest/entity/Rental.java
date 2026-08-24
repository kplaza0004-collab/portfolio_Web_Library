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
@Table(name = "rentals")
@Data
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    // BookHistory の主キー(id: Long)を参照する外部キーカラムを指定
    @ManyToOne
    @JoinColumn(name = "book_history_id")
    private BookHistory bookHistory;

    private LocalDateTime rentalDate;
    private String status; // RENTING, RETURNED
    private LocalDateTime returnedDate;
}