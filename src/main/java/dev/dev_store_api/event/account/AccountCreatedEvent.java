package dev.dev_store_api.event.account;

import dev.dev_store_api.event.AppEvent;
import dev.dev_store_api.model.Account;

public record AccountCreatedEvent(Account account) implements AppEvent {
    @Override
    public String type() {
        return "account.created";
    }
}
