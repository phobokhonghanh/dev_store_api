package dev.dev_store_api.libs.utils.exception;

import dev.dev_store_api.model.type.EMessage;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
