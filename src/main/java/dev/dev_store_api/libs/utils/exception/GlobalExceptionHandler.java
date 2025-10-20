package dev.dev_store_api.libs.utils.exception;

import dev.dev_store_api.factory.ResponseFactory;
import dev.dev_store_api.model.dto.response.BaseResponse;
import dev.dev_store_api.model.type.EMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
                .orElse(String.valueOf(EMessage.VALIDATION_ERROR));
        BaseException exception = new BadRequestException(message);
        return ResponseFactory.error(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleUnknownException(Exception ex) {
        log.error("Internal Server Error: ", ex);
        return ResponseFactory.error(EMessage.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
