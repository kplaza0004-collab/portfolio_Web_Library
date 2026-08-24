package com.example.userTest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // ハッシュ化したいパスワードを指定
        String rawPassword = "12345678"; 
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("暗号化後: " + encodedPassword);
    }
}