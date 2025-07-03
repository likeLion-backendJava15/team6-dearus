package com.diary.global.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String userId;
    private String password;
}