package dev.dev_store_api.common.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends BaseException  {
    public AuthException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
    public AuthException(HttpStatus status, String message) {
        super(status, message);
    }
}
