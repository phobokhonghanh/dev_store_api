package dev.dev_store_api.account.event;

import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.account.repository.AccountRepository;
import dev.dev_store_api.common.config.properties.AppProperties;
import dev.dev_store_api.auth.config.routes.AuthRoutes;
import dev.dev_store_api.common.event.AppEvent;
import dev.dev_store_api.common.event.AppEventHandler;
import dev.dev_store_api.common.model.type.EStatus;
import dev.dev_store_api.common.service.email.EmailService;
import dev.dev_store_api.auth.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class AccountCreatedMailHandler implements AppEventHandler<AccountCreatedEvent> {

    private static final Logger log = LoggerFactory.getLogger(AccountCreatedMailHandler.class);

    private final EmailService emailService;
    private final JwtService jwtService;
    private final AppProperties appProperties;
    private final AccountRepository accountRepository; // Injected repository

    @Override
    public boolean supports(AppEvent event) {
        return event instanceof AccountCreatedEvent;
    }

    @Override
    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000)
    )
    public void handle(AccountCreatedEvent event) {
        var account = event.account();
        var origin = event.origin();
        log.info("Attempting to send verification email to {} from origin {}", account.getEmail(), origin);

        String token = jwtService.generateToken(account.getOtpCode());
        String link = UriComponentsBuilder.newInstance()
                .uri(URI.create(appProperties.api().url()))
                .path(AuthRoutes.VERIFY_OTP)
                .queryParam("token", token)
                .queryParam("redirect_url", origin) // Add origin as redirect_url
                .buildAndExpand(account.getUsername())
                .toUriString();
        emailService.sendLinkVerify(
                account.getEmail(),
                link,
                account.getUsername()
        );
        log.info("Verification email sent successfully to {}", account.getEmail());
    }

    @Recover
    public void recover(RuntimeException e, AccountCreatedEvent event) {
        log.error("Failed to send verification email after multiple retries for user: {}. Error: {}", event.account().getEmail(), e.getMessage());
        Account account = event.account();
        account.setStatus(EStatus.EMAIL_SEND_FAILED.getValue());
        accountRepository.save(account);
    }
}
