package dev.dev_store_api.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.dev_store_api.model.type.EMessage;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class BaseResponse<T> {
    private EMessage message;
    private T data;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
