package com.example.userTest.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.userTest.dto.AdminSearchForm;
import com.example.userTest.dto.MonthlyRentalStat;
import com.example.userTest.entity.Account;
import com.example.userTest.entity.Rental;
import com.example.userTest.entity.Role;
import com.example.userTest.repository.AccountRepository;
import com.example.userTest.repository.RentalRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AccountRepository accountRepository;
    private final RentalRepository rentalRepository;

    public AdminController(AccountRepository accountRepository, RentalRepository rentalRepository) {
        this.accountRepository = accountRepository;
        this.rentalRepository = rentalRepository;
    }

    @GetMapping("/menu")
    public String adminMenu(@ModelAttribute("adminSearchForm") AdminSearchForm searchForm, Model model) {

    	// ★ メソッドの中（処理の最初など）に記述します
//        List<Account> allAccounts = accountRepository.findAll();
//        for (Account acc : allAccounts) {
//            System.out.println("ID: " + acc.getUserId() + ", Role: " + acc.getRole());
//        }
    	
        // ラムダ式内での参照用に final な変数 form を宣言（effectively final の確保）
        final AdminSearchForm form = (searchForm != null) ? searchForm : new AdminSearchForm();

        // 1. 会員IDのメンテナンス（一般ユーザー一覧・10件ずつ）
        List<Account> allUsers = accountRepository.findByRole(Role.USER);
        model.addAttribute("userList", getPagedList(allUsers, form.getUserPage()));
        model.addAttribute("userTotalPages", (int) Math.ceil((double) allUsers.size() / 10));

        // 2. 管理者IDのメンテナンス（管理者一覧・10件ずつ）
        List<Account> allAdmins = accountRepository.findByRole(Role.ADMIN);
        model.addAttribute("adminList", getPagedList(allAdmins, form.getAdminPage()));
        model.addAttribute("adminTotalPages", (int) Math.ceil((double) allAdmins.size() / 10));

        // 3. レンタル中書籍のメンテナンス（フィルタリング＆10件ずつ）
        List<Rental> rentingList = rentalRepository.findAll().stream()
                .filter(r -> "RENTING".equals(r.getStatus()))
                .filter(r -> form.getRentalUserId() == null || form.getRentalUserId().isEmpty() || r.getUserId().equals(form.getRentalUserId()))
                .filter(r -> filterByDateRange(r.getRentalDate(), form.getRentalFromDate(), form.getRentalToDate()))
                .collect(Collectors.toList());

        model.addAttribute("rentingList", getPagedList(rentingList, form.getRentalPage()));
        model.addAttribute("rentingTotalPages", (int) Math.ceil((double) rentingList.size() / 10));

        // 4. レンタル実績集計（月単位12ヶ月分）
        List<MonthlyRentalStat> monthlyStats = calculateMonthlyStats(form);
        model.addAttribute("monthlyStats", monthlyStats);

        // 5. ランキング Best 10（自動集計）
        List<Object[]> userRanking = rentalRepository.findTopUserRanking().stream().limit(10).collect(Collectors.toList());
        List<Object[]> bookRanking = rentalRepository.findTopBookRanking().stream().limit(10).collect(Collectors.toList());

        model.addAttribute("userRanking", userRanking);
        model.addAttribute("bookRanking", bookRanking);

        // Modelへ確実にFormをセット
        model.addAttribute("adminSearchForm", form);

        return "adminMenu";
    }

    // チェックIDの削除（会員・管理者共通）
    @PostMapping("/users/delete")
    public String deleteAccounts(@RequestParam(value = "deleteIds", required = false) List<String> deleteIds) {
        if (deleteIds != null && !deleteIds.isEmpty()) {
            accountRepository.deleteAllById(deleteIds);
        }
        return "redirect:/admin/menu";
    }

    // レンタル中書籍の一括返却（ステータス更新）
    @PostMapping("/rentals/return")
    public String returnRentals(@RequestParam(value = "rentalIds", required = false) List<Long> rentalIds) {
        if (rentalIds != null && !rentalIds.isEmpty()) {
            List<Rental> rentals = rentalRepository.findAllById(rentalIds);
            for (Rental r : rentals) {
                r.setStatus("RETURNED");
                r.setReturnedDate(LocalDateTime.now());
            }
            rentalRepository.saveAll(rentals);
        }
        return "redirect:/admin/menu";
    }

    // --- 内部補助メソッド ---

    private <T> List<T> getPagedList(List<T> list, int page) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        int pageSize = 10;
        int maxPage = Math.max(0, (int) Math.ceil((double) list.size() / (double) pageSize) - 1);
        int currentPage = Math.max(0, Math.min(page, maxPage));
        
        int fromIndex = currentPage * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        
        if (fromIndex >= list.size()) {
            return new ArrayList<>();
        }
        return list.subList(fromIndex, toIndex);
    }

    private boolean filterByDateRange(LocalDateTime target, String fromStr, String toStr) {
        if (target == null) return false;
        LocalDate targetDate = target.toLocalDate();
        if (fromStr != null && !fromStr.isEmpty()) {
            if (targetDate.isBefore(LocalDate.parse(fromStr))) return false;
        }
        if (toStr != null && !toStr.isEmpty()) {
            if (targetDate.isAfter(LocalDate.parse(toStr))) return false;
        }
        return true;
    }

    private List<MonthlyRentalStat> calculateMonthlyStats(AdminSearchForm form) {
        List<MonthlyRentalStat> stats = new ArrayList<>();
        YearMonth endYM = YearMonth.now();
        YearMonth startYM = endYM.minusMonths(11); // デフォルト: 過去1年間 (12ヶ月)

        if (form != null && form.getStatsFromDate() != null && !form.getStatsFromDate().isEmpty()) {
            startYM = YearMonth.parse(form.getStatsFromDate());
        }
        if (form != null && form.getStatsToDate() != null && !form.getStatsToDate().isEmpty()) {
            endYM = YearMonth.parse(form.getStatsToDate());
        }

        LocalDateTime start = startYM.atDay(1).atStartOfDay();
        LocalDateTime end = endYM.atEndOfMonth().atTime(23, 59, 59);

        String userIdParam = (form != null && form.getStatsUserId() != null && !form.getStatsUserId().isEmpty()) 
                ? form.getStatsUserId() : null;

        List<Rental> rentals = rentalRepository.findRentalsForStats(userIdParam, start, end);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth curr = startYM;
        while (!curr.isAfter(endYM)) {
            String ymStr = curr.format(formatter);
            long count = rentals.stream()
                    .filter(r -> r.getRentalDate() != null && r.getRentalDate().format(formatter).equals(ymStr))
                    .count();
            stats.add(new MonthlyRentalStat(ymStr, count));
            curr = curr.plusMonths(1);
        }
        return stats;
    }
}