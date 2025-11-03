package dev.dev_store_api.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String username;
    private String token;
    private String refreshToken;
}
