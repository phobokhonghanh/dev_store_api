package dev.dev_store_api.libs.utils.exception;

import dev.dev_store_api.model.type.EMessage;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException  {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
