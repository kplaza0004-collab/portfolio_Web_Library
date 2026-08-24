package com.example.userTest.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.userTest.dto.BookSearchForm;
import com.example.userTest.dto.GoogleBooksResponse;
import com.example.userTest.form.AccountForm;
import com.example.userTest.form.LoginForm;
import com.example.userTest.service.GoogleBooksService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class BookController {

    private final GoogleBooksService googleBooksService;

    @PostMapping("/search")
    public String searchBooks(@ModelAttribute("searchForm") BookSearchForm searchForm,
                              HttpSession session,
                              Model model) {
        List<GoogleBooksResponse.Item> searchResults = googleBooksService.searchBooks(searchForm);

        session.setAttribute("searchResults", searchResults);

        if (searchResults.isEmpty()) {
            model.addAttribute("searchMessage", "該当する書籍は見つかりませんでした");
        }

        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("accountForm", new AccountForm());
        model.addAttribute("searchResults", searchResults);

        return "login";
    }
}