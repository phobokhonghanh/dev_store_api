package dev.dev_store_api.event;

import dev.dev_store_api.model.Account;
import dev.dev_store_api.service.email.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public class AppEventListener
{
    private final EmailService emailService;

    public AppEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AccountCreatedEvent event) {
        Account account = event.getAccount();
        emailService.sendOtpEmail(
                account.getEmail(),
                account.getOtpCode(),
                account.getUsername()
        );
    }

}

