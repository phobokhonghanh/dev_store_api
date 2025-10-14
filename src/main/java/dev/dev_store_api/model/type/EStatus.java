package dev.dev_store_api.model.type;

import lombok.Getter;

@Getter
public enum EStatus {
    BLOCK(2),
    ACTIVE(1),
    UNACTIVE(0);

    private final int value;

    EStatus(int value) {
        this.value = value;
    }

}
