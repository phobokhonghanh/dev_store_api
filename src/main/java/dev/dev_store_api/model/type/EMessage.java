package dev.dev_store_api.model.type;

import lombok.Getter;

@Getter
public enum EMessage {
    SUCCESS("Success"),
    CREATED("Created successfully"),
    UPDATED("Updated successfully"),
    DELETED("Deleted successfully"),
    FAILED("Operation failed"),
    VALIDATION_ERROR("Validation error"),
    INTERNAL_ERROR("Internal server error");

    private final String message;

    EMessage(String message) {
        this.message = message;
    }
}
