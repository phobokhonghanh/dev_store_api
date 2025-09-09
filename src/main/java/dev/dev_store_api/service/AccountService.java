package dev.dev_store_api.service;

import dev.dev_store_api.libs.utils.GenericMapper;
import dev.dev_store_api.libs.utils.exception.AlreadyExistsException;
import dev.dev_store_api.libs.utils.exception.AuthException;
import dev.dev_store_api.libs.utils.exception.NotFoundException;
import dev.dev_store_api.model.Account;
import dev.dev_store_api.model.ThirdParty;
import dev.dev_store_api.model.dto.AccountDTO;
import dev.dev_store_api.model.dto.AccountResponseDTO;
import dev.dev_store_api.model.dto.request.LoginRequest;
import dev.dev_store_api.model.dto.response.LoginResponse;
import dev.dev_store_api.model.type.EAccount;
import dev.dev_store_api.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final GenericMapper genericMapper;
    private final AccountRoleService accountRoleService;

    public AccountService(AccountRepository accountRepository, GenericMapper genericMapper, AccountRoleService accountRoleService) {
        this.accountRepository = accountRepository;
        this.genericMapper = genericMapper;
        this.accountRoleService = accountRoleService;
    }

    public LoginResponse validateUser(LoginRequest loginRequest) {
        Account account = accountRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("Username not found!"));

        if (!BCrypt.checkpw(loginRequest.getPassword(), account.getPassword())) {
            throw new AuthException("Invalid password!");
        }

        String token = "";
        String refreshToken = "";

        return LoginResponse.builder()
                .username(account.getUsername())
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }


    public void existsByUsername(String username) {
        if (accountRepository.existsByUsername(username)) {
            throw new AlreadyExistsException(String.format("Username '%s' already exists!", username));
        }
    }

    public void existsByEmail(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new AlreadyExistsException(String.format("Email '%s' already exists!", email));
        }
    }
    @Transactional
    public AccountResponseDTO createAccount(AccountDTO accountDTO, String role) {
        existsByUsername(accountDTO.getUsername());
        existsByEmail(accountDTO.getEmail());
        Account account = genericMapper.toEntity(accountDTO, Account.class);
        String hashedPassword = BCrypt.hashpw(accountDTO.getPassword(), BCrypt.gensalt(10));
        account.setPassword(hashedPassword);
        account.setStatus(EAccount.UNACTIVE.getValue());
        account.setThirdParty(new ThirdParty(accountDTO.getThirdParty()));
        account = accountRepository.save(account);
        accountRoleService.create(account,role);
        return genericMapper.toDTO(account, AccountResponseDTO.class);
    }

    public AccountResponseDTO getAccount(String username) {
        Account account = accountRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(String.format("Account with username '%d' not found!", username)));
        return genericMapper.toDTO(account, AccountResponseDTO.class);
    }
}
