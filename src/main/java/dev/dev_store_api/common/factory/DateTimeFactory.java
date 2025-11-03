package dev.dev_store_api.common.factory;

import java.time.LocalDateTime;

public final class DateTimeFactory {

    private DateTimeFactory() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

}
