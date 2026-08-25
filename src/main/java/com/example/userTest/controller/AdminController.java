package com.example.userTest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.userTest.entity.Account;
import com.example.userTest.entity.Role;
import com.example.userTest.repository.AccountRepository;
import com.example.userTest.repository.RentalRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AccountRepository accountRepository;
    private final RentalRepository rentalRepository;

    /**
     * 管理者MENU画面の表示
     */
    @GetMapping("/menu")
    public String adminMenu(Model model) {

        // 1. 会員一覧 (Role.USER) の取得
        List<Account> userList = accountRepository.findByRole(Role.USER);
        model.addAttribute("userList", userList != null ? userList : new ArrayList<>());

        // 2. 管理者一覧 (Role.ADMIN) の取得
        List<Account> adminList = accountRepository.findByRole(Role.ADMIN);
        model.addAttribute("adminList", adminList != null ? adminList : new ArrayList<>());

        // 3. レンタル中書籍一覧の取得（RENTING ステータス）
        try {
            model.addAttribute("rentingList", rentalRepository.findByStatus("RENTING"));
        } catch (Exception e) {
            model.addAttribute("rentingList", new ArrayList<>());
        }

        // 4. ランキングデータの取得
        try {
            model.addAttribute("userRanking", rentalRepository.findTopUserRanking());
            model.addAttribute("bookRanking", rentalRepository.findTopBookRanking());
        } catch (Exception e) {
            model.addAttribute("userRanking", new ArrayList<>());
            model.addAttribute("bookRanking", new ArrayList<>());
        }

        return "admin/menu";
    }

    /**
     * ユーザーID / 管理者IDの削除処理
     */
    @PostMapping("/users/delete")
    public String deleteUsers(
            @RequestParam(name = "deleteIds", required = false) List<String> deleteIds,
            RedirectAttributes redirectAttributes) {

        if (deleteIds != null && !deleteIds.isEmpty()) {
            accountRepository.deleteAllById(deleteIds);
            redirectAttributes.addFlashAttribute("successMessage", "選択したユーザーを削除しました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "削除対象が選択されていません。");
        }

        return "redirect:/admin/menu";
    }
}