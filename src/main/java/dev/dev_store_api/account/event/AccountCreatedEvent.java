package dev.dev_store_api.account.event;

import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.common.event.AppEvent;

public record AccountCreatedEvent(Account account, String origin) implements AppEvent {
    @Override
    public String type() {
        return "account.created";
    }
}
