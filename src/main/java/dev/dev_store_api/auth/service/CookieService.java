package dev.dev_store_api.auth.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    public ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // should be true in production
                .path("/")
                .maxAge(maxAge)
                .sameSite("None") // required for cross-site requests
                .build();
    }

    public ResponseCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true) // should be true in production
                .path("/")
                .maxAge(0)
                .sameSite("None") // required for cross-site requests
                .build();
    }
}
