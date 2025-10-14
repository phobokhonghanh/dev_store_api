package dev.dev_store_api.model.type;

import lombok.Getter;

@Getter
public enum ERole {
    USER(1),
    ADMIN(0);

    private final int value;

    ERole(int value) {
        this.value = value;
    }

}
