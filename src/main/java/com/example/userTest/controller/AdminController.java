package com.example.userTest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.userTest.entity.Account;
import com.example.userTest.entity.Rental;
import com.example.userTest.form.AdminSearchForm;
import com.example.userTest.repository.AccountRepository;
import com.example.userTest.repository.RentalRepository;
import com.example.userTest.service.RentalService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AccountRepository accountRepository;
    private final RentalRepository rentalRepository;
    private final RentalService rentalService;

    /**
     * 管理者MENU画面の表示
     */
    @GetMapping("/menu")
    public String adminMenu(
            @ModelAttribute("adminSearchForm") AdminSearchForm form,
            Model model) {

        // 1. Formがnullの場合の安全初期化
        if (form == null) {
            form = new AdminSearchForm();
        }

        int pageSize = 10;
        int userPage = form.getUserPage() != null ? form.getUserPage() : 0;
        int adminPage = form.getAdminPage() != null ? form.getAdminPage() : 0;

        // 2. 会員一覧 (ROLE_USER) の取得
        Pageable userPageable = PageRequest.of(userPage, pageSize);
        Page<Account> userPageResult = accountRepository.findByRole("ROLE_USER", userPageable);
        model.addAttribute("userList", userPageResult != null ? userPageResult.getContent() : new ArrayList<>());
        model.addAttribute("userTotalPages", userPageResult != null ? userPageResult.getTotalPages() : 0);

        // 3. 管理者一覧 (ROLE_ADMIN) の取得
        Pageable adminPageable = PageRequest.of(adminPage, pageSize);
        Page<Account> adminPageResult = accountRepository.findByRole("ROLE_ADMIN", adminPageable);
        model.addAttribute("adminList", adminPageResult != null ? adminPageResult.getContent() : new ArrayList<>());
        model.addAttribute("adminTotalPages", adminPageResult != null ? adminPageResult.getTotalPages() : 0);

        // 4. レンタル中書籍一覧の取得 (サービス経由)
        List<Rental> rentingList = rentalService.getRentingList(form);
        model.addAttribute("rentingList", rentingList != null ? rentingList : new ArrayList<>());

        // 5. 月別実績集計の取得 (サービス経由)
        List<?> monthlyStats = rentalService.getMonthlyStats(form);
        model.addAttribute("monthlyStats", monthlyStats != null ? monthlyStats : new ArrayList<>());

        // 6. ランキングデータの取得
        List<Object[]> userRanking = rentalRepository.findTopUserRanking();
        model.addAttribute("userRanking", userRanking != null ? userRanking : new ArrayList<>());

        List<Object[]> bookRanking = rentalRepository.findTopBookRanking();
        model.addAttribute("bookRanking", bookRanking != null ? bookRanking : new ArrayList<>());

        model.addAttribute("adminSearchForm", form);

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

    /**
     * レンタル中書籍の強制返却処理
     */
    @PostMapping("/rentals/return")
    public String returnRentals(
            @RequestParam(name = "rentalIds", required = false) List<Long> rentalIds,
            RedirectAttributes redirectAttributes) {

        if (rentalIds != null && !rentalIds.isEmpty()) {
            rentalService.forceReturnRentals(rentalIds);
            redirectAttributes.addFlashAttribute("successMessage", "選択した書籍を返却済みに更新しました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "対象の書籍が選択されていません。");
        }

        return "redirect:/admin/menu";
    }
}