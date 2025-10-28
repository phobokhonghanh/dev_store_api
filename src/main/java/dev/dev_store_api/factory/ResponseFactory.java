package dev.dev_store_api.factory;

import dev.dev_store_api.model.dto.response.BaseResponse;
import dev.dev_store_api.model.type.EMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseFactory {
    public static <T> ResponseEntity<BaseResponse<T>> success(T data, String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(BaseResponse.<T>builder()
                        .message(message)
                        .data(data)
                        .timestamp(DateTimeFactory.now())
                        .build());
    }
    public static <T> ResponseEntity<BaseResponse<T>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(BaseResponse.<T>builder()
                        .message(message)
                        .data(null)
                        .timestamp(DateTimeFactory.now())
                        .build());
    }
}
