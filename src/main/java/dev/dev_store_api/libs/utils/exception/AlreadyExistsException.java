package dev.dev_store_api.libs.utils.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends BaseException  {
    public AlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
