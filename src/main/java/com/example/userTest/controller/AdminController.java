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

        // 1. 会員一覧 (ROLE_USER) の取得
        List<Account> userList = accountRepository.findByRole(Role.ROLE_USER);
        model.addAttribute("userList", userList != null ? userList : new ArrayList<>());

        // 2. 管理者一覧 (ROLE_ADMIN) の取得
        List<Account> adminList = accountRepository.findByRole(Role.ROLE_ADMIN);
        model.addAttribute("adminList", adminList != null ? adminList : new ArrayList<>());

        // 3. レンタル中書籍一覧の取得（RENTING ステータス）
        model.addAttribute("rentingList", rentalRepository.findByUserIdAndStatus(null, "RENTING"));

        // 4. ランキングデータの取得
        model.addAttribute("userRanking", rentalRepository.findTopUserRanking());
        model.addAttribute("bookRanking", rentalRepository.findTopBookRanking());

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