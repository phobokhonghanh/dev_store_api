package dev.dev_store_api.common.model.type;

import lombok.Getter;

@Getter
public enum EStatus {
    BLOCK(2),
    ACTIVE(1),
    UNACTIVE(0),
    EMAIL_SEND_FAILED(-1);

    private final int value;

    EStatus(int value) {
        this.value = value;
    }

}
