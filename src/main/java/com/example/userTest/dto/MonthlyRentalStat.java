package com.example.userTest.dto;

public class MonthlyRentalStat {
    private String month; // YYYY-MM
    private long count;   // 貸出冊数

    public MonthlyRentalStat(String month, long count) {
        this.month = month;
        this.count = count;
    }

    public String getMonth() { return month; }
    public long getCount() { return count; }
}

//package com.example.userTest.dto;
//
//public class MonthlyRentalStat {
//
//}
