package dev.dev_store_api.model.type;

public enum EAccount {
    ACTIVE(1),
    UNACTIVE(0);

    private final int value;

    EAccount(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
