package dev.dev_store_api.common.model.type;

import lombok.Getter;

@Getter
public enum EMessage {
    SUCCESS("Success"),
    CREATED("Created successfully"),
    UPDATED("Updated successfully"),
    DELETED("Deleted successfully"),
    FAILED("Operation failed"),
    VALIDATION_ERROR("Validation error"),
    INVALID("Invalid %s"),
    EXPIRED_INVALID("Token expired or invalid signature"),
    EXPIRED_TOKEN("Token expired"),
    INTERNAL_ERROR("Internal server error"),
    NOT_FOUND("Not found %s %s"),
    REFRESH_TOKEN_REQUIRED("Refresh token is required"),
    DEACTIVATED("This device has been deactivated. Please contact support."),

    ;
    private final String message;

    EMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(this.message, args);
    }

}
