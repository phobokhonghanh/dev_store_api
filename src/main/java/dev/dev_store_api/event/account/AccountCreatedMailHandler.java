package dev.dev_store_api.event.account;

import dev.dev_store_api.event.AppEvent;
import dev.dev_store_api.event.AppEventHandler;
import dev.dev_store_api.libs.constant.properties.ApiProperties;
import dev.dev_store_api.libs.constant.routes.ApiRoutes;
import dev.dev_store_api.service.email.EmailService;
import dev.dev_store_api.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class AccountCreatedMailHandler implements AppEventHandler<AccountCreatedEvent> {

    private final EmailService emailService;
    private final JwtService jwtService;
    private final ApiProperties apiProperties;

    @Override
    public boolean supports(AppEvent event) {
        return event instanceof AccountCreatedEvent;
    }

    @Override
    public void handle(AccountCreatedEvent event) {
        var account = event.account();
        String token = jwtService.generateToken(account.getOtpCode());
        String link = UriComponentsBuilder.newInstance()
                .uri(URI.create(apiProperties.url()))
                .path(ApiRoutes.VERIFY_OTP)
                .queryParam("token", token)
                .buildAndExpand(account.getUsername())
                .toUriString();
        emailService.sendLinkVerify(
                account.getEmail(),
                link,
                account.getUsername()
        );
    }
}
