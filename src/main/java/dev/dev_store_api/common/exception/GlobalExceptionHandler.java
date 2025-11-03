package dev.dev_store_api.common.exception;

import dev.dev_store_api.common.factory.ResponseFactory;
import dev.dev_store_api.common.dto.BaseResponse;
import dev.dev_store_api.common.model.type.EMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Locale;

import org.springframework.web.bind.MissingRequestHeaderException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void>> handleException(BaseException ex) {
        return ResponseFactory.error(ex.getMessage(), ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(EMessage.VALIDATION_ERROR.getMessage());
        BaseException exception = new BadRequestException(message);
        return ResponseFactory.error(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("Request method '%s' is not supported. Supported methods are %s.",
                ex.getMethod(),
                ex.getSupportedHttpMethods());
        return ResponseFactory.error(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<BaseResponse<Void>> handleMissingHeaderException(MissingRequestHeaderException ex) {
        String message = String.format("Required request header '%s' is not present.", ex.getHeaderName());
        return ResponseFactory.error(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleUnknownException(Exception ex) {
        log.error("Internal Server Error: ", ex);
        return ResponseFactory.error(EMessage.INTERNAL_ERROR.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
