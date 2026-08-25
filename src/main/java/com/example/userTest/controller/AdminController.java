package com.example.userTest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.userTest.entity.Account;
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
    public String adminMenu(
            @RequestParam(name = "userPage", defaultValue = "0") int userPage,
            @RequestParam(name = "adminPage", defaultValue = "0") int adminPage,
            Model model) {

        int pageSize = 10;

        // 1. 会員一覧 (ROLE_USER) の取得
        Pageable userPageable = PageRequest.of(userPage, pageSize);
        Page<Account> userPageResult = accountRepository.findByRole("ROLE_USER", userPageable);
        model.addAttribute("userList", userPageResult != null ? userPageResult.getContent() : new ArrayList<>());
        model.addAttribute("userTotalPages", userPageResult != null ? userPageResult.getTotalPages() : 0);

        // 2. 管理者一覧 (ROLE_ADMIN) の取得
        Pageable adminPageable = PageRequest.of(adminPage, pageSize);
        Page<Account> adminPageResult = accountRepository.findByRole("ROLE_ADMIN", adminPageable);
        model.addAttribute("adminList", adminPageResult != null ? adminPageResult.getContent() : new ArrayList<>());
        model.addAttribute("adminTotalPages", adminPageResult != null ? adminPageResult.getTotalPages() : 0);

        // 3. レンタル中書籍一覧の取得（RENTING ステータスで検索）
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