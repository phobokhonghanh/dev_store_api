package dev.dev_store_api.common.event;

public interface AppEventHandler<T extends AppEvent> {
    boolean supports(AppEvent event);
    void handle(T event);
}