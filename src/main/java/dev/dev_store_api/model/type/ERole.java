package dev.dev_store_api.model.type;

public enum ERole {
    USER(1),
    ADMIN(0);

    private final int value;

    ERole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
