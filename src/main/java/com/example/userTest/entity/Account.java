package com.example.userTest.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "id") // DBのカラム名 'id' にマッピング
    private String userId;

    @Column(length = 100) // ⭕ BCrypt(60文字)が入るよう余裕を持たせる
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at") // DBのカラム名 'created_at' にマッピング
    private LocalDateTime createdAt;

    @Column(name = "last_login_at") // DBのカラム名 'last_login_at' にマッピング
    private LocalDateTime lastLoginAt;

    // --- コンストラクタ ---
    public Account() {
    }

 // Account.java 内に追加
    public Account(String userId, String password, Role role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now(); // 登録日時を自動設定
        this.lastLoginAt = null;
    }

    // --- Getter / Setter ---
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
 // Account.java の末尾などに追加
    public String getId() {
        return userId;
    }

    public void setId(String id) {
        this.userId = id;
    }
}
