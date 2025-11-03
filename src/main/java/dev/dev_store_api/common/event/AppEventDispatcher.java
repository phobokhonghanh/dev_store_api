package dev.dev_store_api.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AppEventDispatcher {

    private final List<AppEventHandler<?>> handlers;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dispatch(AppEvent event) {
        handlers.stream()
                .filter(h -> h.supports(event))
                .forEach(h -> {
                    ((AppEventHandler<AppEvent>) h).handle(event);
                });
    }
}
