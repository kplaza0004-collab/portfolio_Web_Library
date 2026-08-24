package com.example.userTest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; // 追加

import lombok.Data;

@Entity
@Table(name = "book_history") // テーブル名を明示的に指定
//@Table(name = "book_history") // テーブル名を明示的に指定
@Data
public class BookHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 191) // UNIQUE制約と長さの調整
    private String googleBookId;
    
    private String title;
    private String subtitle;
    
    private String authors; 
    private String publisher;
    
    private String publishedDate;
    
    @Column(columnDefinition = "TEXT") // 長い文章に対応できるよう TEXT 型に変更
    private String description;
    
    private Integer pageCount;
    private String canonicalVolumeLink;
    
    @Column(length = 1000)
    private String imageLink;
}