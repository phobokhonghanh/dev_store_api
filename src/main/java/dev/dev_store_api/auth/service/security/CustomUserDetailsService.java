package dev.dev_store_api.auth.service.security;

import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.account.service.AccountLookupService;
import dev.dev_store_api.account.service.AccountService;
import dev.dev_store_api.common.exception.AuthException;
import dev.dev_store_api.common.exception.NotFoundException;
import dev.dev_store_api.common.model.type.EStatus;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountLookupService accountLookupService;

    public CustomUserDetailsService(AccountLookupService accountLookupService) {
        this.accountLookupService = accountLookupService;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        try {
            Account account = accountLookupService.findAccountByIdentifier(identifier);
            if(account.getStatus() != EStatus.ACTIVE.getValue()) {
                throw new AuthException(HttpStatus.FORBIDDEN, "Account FORBIDDEN");
            }
            return org.springframework.security.core.userdetails.User.builder()
                    .username(account.getUsername())
                    .password(account.getPassword())
                    .authorities("ROLE_USER")
                    .build();
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("User not found or inactive: " + identifier);
        }
    }
}