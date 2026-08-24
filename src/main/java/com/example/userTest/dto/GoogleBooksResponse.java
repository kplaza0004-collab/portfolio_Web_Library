package com.example.userTest.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksResponse {
    private int totalItems; // 検索結果の総件数
    private List<Item> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String id; // 書籍ID（詳細表示やDB保管時に必須）
        private VolumeInfo volumeInfo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VolumeInfo {
        // --- 既存フィールド ---
        private String title;
        private List<String> authors;
        private String publisher;

        // --- 追加フィールド ---
        private String subtitle;               // サブタイトル
        private String publishedDate;          // 出版日
        private String description;            // 説明・概要
        private Integer pageCount;             // ページ数
        private String canonicalVolumeLink;   // Google Books リンク
        private ImageLinks imageLinks;         // 画像リンクオブジェクト
        private List<IndustryIdentifier> industryIdentifiers; // ISBN情報リスト

        // サムネイル画像用インナークラス
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ImageLinks {
            private String smallThumbnail;
            private String thumbnail;
        }

        // ISBN情報用インナークラス
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class IndustryIdentifier {
            private String type;       // 例: "ISBN_10", "ISBN_13"
            private String identifier; // ISBN番号
        }
    }
}
