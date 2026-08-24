package com.example.userTest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.userTest.dto.BookSearchForm;
import com.example.userTest.dto.GoogleBooksResponse;
import com.example.userTest.entity.BookHistory;
import com.example.userTest.repository.BookHistoryRepository;

@Service
public class GoogleBooksService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BookHistoryRepository bookHistoryRepository;

    @Value("${google.api.key}")
    private String apiKey;

    // BookHistoryRepository を Spring からインジェクション
    public GoogleBooksService(BookHistoryRepository bookHistoryRepository) {
        this.bookHistoryRepository = bookHistoryRepository;
    }

    /**
     * 条件を指定して Google Books API から検索 (最大30件取得)
     */
    public List<GoogleBooksResponse.Item> searchBooks(BookSearchForm form) {
        StringBuilder query = new StringBuilder();
        
        if (form.getTitle() != null && !form.getTitle().isBlank()) {
            query.append("intitle:").append(form.getTitle()).append(" ");
        }
        if (form.getAuthor() != null && !form.getAuthor().isBlank()) {
            query.append("inauthor:").append(form.getAuthor()).append(" ");
        }
        if (form.getPublisher() != null && !form.getPublisher().isBlank()) {
            query.append("inpublisher:").append(form.getPublisher()).append(" ");
        }
        if (form.getIsbn() != null && !form.getIsbn().isBlank()) {
            query.append("isbn:").append(form.getIsbn()).append(" ");
        }

        String q = query.toString().trim();
        if (q.isEmpty()) {
            return new ArrayList<>();
        }

        // 要求仕様に従い maxResults=30 で設定
        String url = UriComponentsBuilder.fromUriString("https://www.googleapis.com/books/v1/volumes")
                .queryParam("q", q)
                .queryParam("maxResults", 30)
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        try {
            GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);
            if (response != null && response.getItems() != null) {
                return response.getItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Google Book ID から単一の書籍詳細情報を取得
     */
    public GoogleBooksResponse.Item getBookById(String volumeId) {
        String url = UriComponentsBuilder.fromUriString("https://www.googleapis.com/books/v1/volumes/" + volumeId)
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        try {
            return restTemplate.getForObject(url, GoogleBooksResponse.Item.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * レンタル・お気に入り登録時に、本情報を DB (book_history) に保存（存在しなければ登録）
     */
    public BookHistory saveOrGetBookHistory(GoogleBooksResponse.Item item) {
        if (item == null || item.getId() == null) {
            return null;
        }

     // 【修正点】 findById ではなく findByGoogleBookId を使用します
        return bookHistoryRepository.findByGoogleBookId(item.getId()).orElseGet(() -> {
            BookHistory bh = new BookHistory();
            bh.setGoogleBookId(item.getId());

            GoogleBooksResponse.VolumeInfo info = item.getVolumeInfo();
            if (info != null) {
                bh.setTitle(info.getTitle() != null ? info.getTitle() : "タイトル不明");
                bh.setSubtitle(info.getSubtitle());
                bh.setAuthors(info.getAuthors() != null ? String.join(", ", info.getAuthors()) : "著者不明");
                bh.setPublisher(info.getPublisher() != null ? info.getPublisher() : "出版社不明");
                bh.setPublishedDate(info.getPublishedDate());
                bh.setDescription(info.getDescription());
                bh.setPageCount(info.getPageCount());
                bh.setCanonicalVolumeLink(info.getCanonicalVolumeLink());

                if (info.getImageLinks() != null) {
                    bh.setImageLink(info.getImageLinks().getThumbnail());
                }
            }
            return bookHistoryRepository.save(bh);
        });
    }
}