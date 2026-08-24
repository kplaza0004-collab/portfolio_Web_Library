package com.example.userTest.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountForm {

    @Pattern(regexp = "^[A-Z][a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{7}$", 
             message = "半角8桁、先頭英大文字で入力してください")
    private String id;

    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,12}$", 
             message = "8～12桁以上半角英数記号で入力してください")
    private String password;

    private String confirmPassword;

    // パスワード一致チェック（メソッド名が is... で始まる必要があります）
    @AssertTrue(message = "パスワードが一致していません")
    public boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) {
            return true;
        }
        return password.equals(confirmPassword);
    }
}
