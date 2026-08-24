package com.example.userTest.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.userTest.entity.Favorite;
import com.example.userTest.service.FavoriteService;
import com.example.userTest.service.GoogleBooksService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final GoogleBooksService googleBooksService;

    // お気に入り一覧画面の表示
    @GetMapping
    public String showFavorites(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        List<Favorite> favorites = favoriteService.getFavoritesByUserId(principal.getName());
        model.addAttribute("favorites", favorites);
        return "favorites"; // favorites.html へ
    }

    // お気に入り追加
    @PostMapping("/add/{volumeId}")
    public String addFavorite(@PathVariable("volumeId") String volumeId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        // Google Books APIから詳細を取得して DB (book_history) に保存後、お気に入り登録
        var bookItem = googleBooksService.getBookById(volumeId);
        if (bookItem != null) {
            googleBooksService.saveOrGetBookHistory(bookItem);
            favoriteService.addFavorite(principal.getName(), volumeId);
        }
        return "redirect:/bookDetail/" + volumeId;
    }

    // お気に入り削除
    @PostMapping("/remove/{volumeId}")
    public String removeFavorite(@PathVariable("volumeId") String volumeId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        favoriteService.removeFavorite(principal.getName(), volumeId);
        return "redirect:/favorites";
    }
}
