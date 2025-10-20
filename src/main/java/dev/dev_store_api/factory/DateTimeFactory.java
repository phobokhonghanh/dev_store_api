package dev.dev_store_api.factory;

import java.time.LocalDateTime;

public final class DateTimeFactory {

    private DateTimeFactory() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

}
