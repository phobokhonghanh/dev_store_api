package dev.dev_store_api.auth.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    public ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // should be true in production
//                .sameSite("None") // required for cross-site requests
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    public ResponseCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false) // should be true in production
//                .sameSite("None") // required for cross-site requests
                .path("/")
                .maxAge(0)
                .build();
    }
}
