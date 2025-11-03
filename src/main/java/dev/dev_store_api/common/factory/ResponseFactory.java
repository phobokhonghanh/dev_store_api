package dev.dev_store_api.common.factory;

import dev.dev_store_api.common.dto.BaseResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseFactory {
    public static <T> ResponseEntity<BaseResponse<T>> success(T data, String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(successBody(data, message));
    }

    public static <T> ResponseEntity<BaseResponse<T>> success(T data, String message, HttpStatus status, HttpHeaders headers) {
        return ResponseEntity.status(status)
                .headers(headers)
                .body(successBody(data, message));
    }

    public static <T> BaseResponse<T> successBody(T data, String message) {
        return BaseResponse.<T>builder()
                .message(message)
                .data(data)
                .timestamp(DateTimeFactory.now())
                .build();
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
