package com.example.userTest.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.userTest.dto.BookSearchForm;
import com.example.userTest.dto.GoogleBooksResponse; // ★インポートを追加
import com.example.userTest.entity.Role;
import com.example.userTest.form.AccountForm;
import com.example.userTest.form.LoginForm;
import com.example.userTest.service.AccountService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @GetMapping("/")
    public String index() {
        // ログイン画面のパスへリダイレクト
        return "redirect:/login"; 
    }

    // --- 会員ログイン・登録画面（初期表示） ---
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, 
                                HttpSession session,
                                Model model) {
        if (error != null) {
            model.addAttribute("loginError", "IDもしくはパスワードが違います。");
        }
        
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("accountForm", new AccountForm());
        model.addAttribute("searchForm", new BookSearchForm());
        
        // ★ GoogleBooksResponse.Item に変更
        @SuppressWarnings("unchecked")
        List<GoogleBooksResponse.Item> searchResults = (List<GoogleBooksResponse.Item>) session.getAttribute("searchResults");
        if (searchResults == null) {
            searchResults = new ArrayList<>();
        }
        model.addAttribute("searchResults", searchResults);
        
        return "login";
    }

    // --- 新規会員登録処理 ---
    @PostMapping("/register")
    public String registerUser(@Validated @ModelAttribute("accountForm") AccountForm form,
                               BindingResult result,
                               HttpSession session,
                               Model model) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("searchForm", new BookSearchForm());
        
        // ★ GoogleBooksResponse.Item に変更
        @SuppressWarnings("unchecked")
        List<GoogleBooksResponse.Item> searchResults = (List<GoogleBooksResponse.Item>) session.getAttribute("searchResults");
        if (searchResults == null) {
            searchResults = new ArrayList<>();
        }
        model.addAttribute("searchResults", searchResults);

        if (result.hasErrors()) {
            return "login";
        }

        if (accountService.existsById(form.getId())) {
            model.addAttribute("registerError", "このIDは既に登録されています。");
            return "login";
        }

        accountService.registerAccount(form, Role.USER);
        model.addAttribute("successMessage", "登録されました。ログオンが可能になりました");
        model.addAttribute("accountForm", new AccountForm());
        return "login";
    }

    // --- 管理者ログイン画面（表示） ---
    @GetMapping("/adminLogin")
    public String showAdminLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "IDもしくはパスワードが違います。");
        }
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("accountForm", new AccountForm());
        return "adminLogin";
    }

    // --- 管理者登録処理 ---
    @PostMapping("/adminRegister")
    public String registerAdmin(@Validated @ModelAttribute("accountForm") AccountForm form,
                                BindingResult result, Model model) {
        model.addAttribute("loginForm", new LoginForm());

        if (result.hasErrors()) {
            return "adminLogin";
        }

        if (accountService.existsById(form.getId())) {
            model.addAttribute("registerError", "このIDは既に登録されています。");
            return "adminLogin";
        }

        accountService.registerAccount(form, Role.ADMIN);
        model.addAttribute("successMessage", "登録されました。ログオンが可能になりました");
        model.addAttribute("accountForm", new AccountForm());
        return "adminLogin";
    }
}