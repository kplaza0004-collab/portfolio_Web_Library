package com.example.userTest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchForm {

    // ページネーション用
    private int userPage = 0;
    private int adminPage = 0;
    private int rentalPage = 0;

    // 絞り込み検索用
    private String rentalUserId;
    private String rentalFromDate;
    private String rentalToDate;

    // 集計用
    private String statsUserId;
    private String statsFromDate;
    private String statsToDate;
}