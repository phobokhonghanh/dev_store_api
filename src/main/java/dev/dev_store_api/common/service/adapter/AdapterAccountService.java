package dev.dev_store_api.common.service.adapter;

import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.account.repository.AccountRepository;
import dev.dev_store_api.auth.service.security.JwtService;
import dev.dev_store_api.common.exception.NotFoundException;
import dev.dev_store_api.common.model.type.EMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class AdapterAccountService {
    private final AccountRepository accountRepository;
    private final JwtService jwtService;

    public AdapterAccountService(AccountRepository accountRepository, JwtService jwtService) {
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
    }
    public Account findAccountByIdentifier(String identifier) {
        return Stream.<Supplier<Optional<Account>>>of(
                        () -> accountRepository.findByUsername(identifier),
                        () -> accountRepository.findByEmail(identifier)
                )
                .map(Supplier::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(EMessage.NOT_FOUND.format("identifier", identifier)));
    }

    public Account getAccountByToken(String token) {
        String identifier = jwtService.extract(token);
        return findAccountByIdentifier(identifier);
    }
}
