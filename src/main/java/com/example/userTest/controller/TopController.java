package com.example.userTest.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.userTest.dto.BookSearchForm;
import com.example.userTest.dto.GoogleBooksResponse;
import com.example.userTest.entity.BookHistory;
import com.example.userTest.entity.Favorite;
import com.example.userTest.entity.Rental;
import com.example.userTest.repository.FavoriteRepository;
import com.example.userTest.repository.RentalRepository;
import com.example.userTest.service.FavoriteService;
import com.example.userTest.service.GoogleBooksService;

@Controller
public class TopController {

    private final GoogleBooksService googleBooksService;
    private final RentalRepository rentalRepository;
    private final FavoriteRepository favoriteRepository;
    private final FavoriteService favoriteService;

    public TopController(GoogleBooksService googleBooksService,
                         RentalRepository rentalRepository,
                         FavoriteRepository favoriteRepository,
                         FavoriteService favoriteService) {
        this.googleBooksService = googleBooksService;
        this.rentalRepository = rentalRepository;
        this.favoriteRepository = favoriteRepository;
        this.favoriteService = favoriteService;
    }

    // 会員TOPページ＆詳細検索表示
    @GetMapping("/top")
    public String top(@ModelAttribute BookSearchForm searchForm,
                      @RequestParam(defaultValue = "0") int page,
                      Principal principal,
                      HttpSession session,
                      Model model) {

        if (principal == null) {
            return "redirect:/login";
        }
        // ⭕ ログインしている実際の会員ID（A1234567, B1234567等）を取得
        String userId = principal.getName();

        // 1. Google Books API 検索処理
        List<GoogleBooksResponse.Item> allResults = googleBooksService.searchBooks(searchForm);

        int pageSize = 10;
        int totalItems = allResults.size();
        int fromIndex = Math.min(page * pageSize, totalItems);
        int toIndex = Math.min(fromIndex + pageSize, totalItems);
        List<GoogleBooksResponse.Item> pagedResults = allResults.subList(fromIndex, toIndex);

        model.addAttribute("searchResults", pagedResults);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) totalItems / pageSize));
        model.addAttribute("searchForm", searchForm);

        // 2. レンタル用一時カートのセッション初期化
        if (session.getAttribute("rentalCart") == null) {
            session.setAttribute("rentalCart", new ArrayList<GoogleBooksResponse.Item>());
        }
        model.addAttribute("rentalCart", session.getAttribute("rentalCart"));

        // 3. DBからログインユーザー自身のレンタル中・お気に入りリストを取得
        List<Rental> rentingList = rentalRepository.findByUserIdAndStatus(userId, "RENTING");
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        model.addAttribute("rentingList", rentingList);
        model.addAttribute("favorites", favorites);

        return "top";
    }

    // レンタルカートに追加
    @PostMapping("/top/cart/rental/add")
    public String addToRentalCart(@RequestParam String bookId,
                                  @ModelAttribute BookSearchForm searchForm,
                                  @RequestParam(defaultValue = "0") int page,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        @SuppressWarnings("unchecked")
        List<GoogleBooksResponse.Item> cart = (List<GoogleBooksResponse.Item>) session.getAttribute("rentalCart");
        GoogleBooksResponse.Item item = googleBooksService.getBookById(bookId);

        if (item != null && cart != null && cart.stream().noneMatch(i -> i.getId().equals(bookId))) {
            cart.add(item);
        }

        redirectAttributes.addAttribute("title", searchForm.getTitle());
        redirectAttributes.addAttribute("author", searchForm.getAuthor());
        redirectAttributes.addAttribute("publisher", searchForm.getPublisher());
        redirectAttributes.addAttribute("isbn", searchForm.getIsbn());
        redirectAttributes.addAttribute("fromDate", searchForm.getFromDate());
        redirectAttributes.addAttribute("toDate", searchForm.getToDate());
        redirectAttributes.addAttribute("page", page);

        return "redirect:/top";
    }

    // お気に入りへ追加
    @PostMapping("/top/cart/favorite/add")
    public String addToFavoriteCart(@RequestParam String bookId,
                                    @ModelAttribute BookSearchForm searchForm,
                                    @RequestParam(defaultValue = "0") int page,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        String userId = principal.getName();

        // 1. 書籍情報の保存
        GoogleBooksResponse.Item item = googleBooksService.getBookById(bookId);
        if (item != null) {
            googleBooksService.saveOrGetBookHistory(item);
        }

        // 2. お気に入り登録
        favoriteService.addFavorite(userId, bookId);

        redirectAttributes.addAttribute("title", searchForm.getTitle());
        redirectAttributes.addAttribute("author", searchForm.getAuthor());
        redirectAttributes.addAttribute("publisher", searchForm.getPublisher());
        redirectAttributes.addAttribute("isbn", searchForm.getIsbn());
        redirectAttributes.addAttribute("fromDate", searchForm.getFromDate());
        redirectAttributes.addAttribute("toDate", searchForm.getToDate());
        redirectAttributes.addAttribute("page", page);

        return "redirect:/top";
    }

    // お気に入り削除
    @PostMapping("/favorites/remove")
    public String removeFavorite(@RequestParam String bookId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        favoriteService.removeFavorite(principal.getName(), bookId);

        return "redirect:/top";
    }

    // レンタル実行 (DB登録 & 履歴保存)
    @PostMapping("/top/rental/execute")
    public String executeRental(Principal principal, HttpSession session) {
        if (principal == null) {
            return "redirect:/login";
        }
        String userId = principal.getName();

        @SuppressWarnings("unchecked")
        List<GoogleBooksResponse.Item> cart = (List<GoogleBooksResponse.Item>) session.getAttribute("rentalCart");

        if (cart != null && !cart.isEmpty()) {
            if (rentalRepository.countByUserIdAndStatus(userId, "RENTING") + cart.size() <= 30) {
                for (GoogleBooksResponse.Item item : cart) {
                    BookHistory bh = googleBooksService.saveOrGetBookHistory(item);
                    Rental rental = new Rental();
                    rental.setUserId(userId); // ⭕ 正しい会員IDがセットされます
                    rental.setBookHistory(bh);
                    rental.setRentalDate(LocalDateTime.now());
                    rental.setStatus("RENTING");
                    rentalRepository.save(rental);
                }
                cart.clear();
            }
        }
        return "redirect:/top";
    }

    // 返却実行
    @PostMapping("/top/rental/return")
    public String returnBook(@RequestParam Long rentalId) {
        rentalRepository.findById(rentalId).ifPresent(rental -> {
            rental.setStatus("RETURNED");
            rental.setReturnedDate(LocalDateTime.now());
            rentalRepository.save(rental);
        });
        return "redirect:/top";
    }
}