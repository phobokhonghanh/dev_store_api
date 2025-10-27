package dev.dev_store_api.model.type;

import dev.dev_store_api.libs.utils.exception.NotFoundException;
import lombok.Getter;

@Getter
public enum EMessage {
    SUCCESS("Success"),
    CREATED("Created successfully"),
    UPDATED("Updated successfully"),
    DELETED("Deleted successfully"),
    FAILED("Operation failed"),
    VALIDATION_ERROR("Validation error"),
    INVALID("Invalid"),
    INTERNAL_ERROR("Internal server error"),
    NOT_FOUND("Not found %s %s"),
    REFRESH_TOKEN_REQUIRED("Refresh token is required"),
    ;
    private final String message;

    EMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(this.message, args);
    }

}
