package com.example.userTest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.userTest.dto.GoogleBooksResponse; // ★インポートを変更
import com.example.userTest.service.GoogleBooksService;

@Controller
public class BookDetailController {

    private final GoogleBooksService googleBooksService;

    public BookDetailController(GoogleBooksService googleBooksService) {
        this.googleBooksService = googleBooksService;
    }

    @GetMapping("/bookDetail/{id}")
    public String bookDetail(@PathVariable("id") String id, Model model) {
        // ★ 型を GoogleBooksResponse.Item に変更
        GoogleBooksResponse.Item book = googleBooksService.getBookById(id);
        model.addAttribute("book", book);
        return "bookDetail";
    }
}