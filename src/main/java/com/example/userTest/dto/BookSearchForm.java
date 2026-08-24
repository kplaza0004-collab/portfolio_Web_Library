package com.example.userTest.dto;

public class BookSearchForm {
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String fromDate; // ★ 追加
    private String toDate;   // ★ 追加

    // --- Getter / Setter ---
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // ★ 追加
    public String getFromDate() {
        return fromDate;
    }

    // ★ 追加
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    // ★ 追加
    public String getToDate() {
        return toDate;
    }

    // ★ 追加
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}

//import lombok.Data;
//
//@Data
//public class BookSearchForm {
//    private String title;
//    private String author;
//    private String publisher;
//    private String isbn; // ← これを追加
//}

