package dev.dev_store_api.account.service;

import dev.dev_store_api.account.dto.AccountDTO;
import dev.dev_store_api.account.dto.AccountResponse;
import dev.dev_store_api.account.dto.UpdateRequest;
import dev.dev_store_api.account.event.AccountCreatedEvent;
import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.account.model.AccountRelation;
import dev.dev_store_api.account.repository.AccountRelationRepository;
import dev.dev_store_api.account.repository.AccountRepository;
import dev.dev_store_api.account.repository.AccountRoleRepository;
import dev.dev_store_api.auth.dto.LoginRequest;
import dev.dev_store_api.auth.dto.LoginResponse;
import dev.dev_store_api.auth.factory.AuthFactory;
import dev.dev_store_api.auth.service.CookieService;
import dev.dev_store_api.auth.service.MultiAgentService;
import dev.dev_store_api.auth.service.security.JwtService;
import dev.dev_store_api.common.config.properties.JwtProperties;
import dev.dev_store_api.common.exception.AlreadyExistsException;
import dev.dev_store_api.common.exception.AuthException;
import dev.dev_store_api.common.exception.BadRequestException;
import dev.dev_store_api.common.exception.NotFoundException;
import dev.dev_store_api.common.model.type.EMessage;
import dev.dev_store_api.common.model.type.EProvider;
import dev.dev_store_api.common.model.type.ERole;
import dev.dev_store_api.common.model.type.EStatus;
import dev.dev_store_api.common.util.GenericMapper;
import dev.dev_store_api.common.util.GenericUpdater;
import dev.dev_store_api.common.util.Libs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AccountRelationRepository relationRepository;
    private final GenericMapper genericMapper;
    private final GenericUpdater genericUpdater;
    private final AccountRoleService accountRoleService;
    private final JwtService jwtService;
    private final MultiAgentService multiAgentService;
    private final AuthFactory authFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final CookieService cookieService;
    private final JwtProperties jwtProperties;
    private final AccountLookupService accountLookupService;

    public AccountServiceImpl(AccountRepository accountRepository, AccountRoleRepository accountRoleRepository,
                              AccountRelationRepository relationRepository,
                              GenericMapper genericMapper, GenericUpdater genericUpdater,
                              AccountRoleService accountRoleService,
                              JwtService jwtService, MultiAgentService multiAgentService,
                              AuthFactory authFactory,
                              ApplicationEventPublisher eventPublisher,
                              CookieService cookieService,
                              JwtProperties jwtProperties, AccountLookupService accountLookupService) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.relationRepository = relationRepository;
        this.genericMapper = genericMapper;
        this.genericUpdater = genericUpdater;
        this.accountRoleService = accountRoleService;
        this.jwtService = jwtService;
        this.multiAgentService = multiAgentService;
        this.authFactory = authFactory;
        this.eventPublisher = eventPublisher;
        this.cookieService = cookieService;
        this.jwtProperties = jwtProperties;
        this.accountLookupService = accountLookupService;
    }

    public String getAccountStatusByEmail(String email) {
        Account account = accountLookupService.getAccountByEmail(email);
        return EStatus.values()[account.getStatus()].name();
    }

    @Transactional
    public AccountResponse registerUser(AccountDTO dto, String origin) {
        return registerSystem(dto, ERole.USER, origin);
    }

    @Transactional
    public AccountResponse registerAdmin(AccountDTO dto, String origin) {
        return registerSystem(dto, ERole.ADMIN, origin);
    }

    @Transactional
    public AccountResponse registerWithFacebook(AccountDTO dto) {
        return registerExternal(dto, EProvider.FACEBOOK);
    }

    @Transactional
    public AccountResponse registerWithGoogle(AccountDTO dto) {
        return registerExternal(dto, EProvider.GOOGLE);
    }

    private AccountResponse registerExternal(AccountDTO dto, EProvider provider) {
        Account account = createAccount(dto, ERole.USER, provider);
        return genericMapper.toDTO(account, AccountResponse.class);
    }

    private AccountResponse registerSystem(AccountDTO dto, ERole role, String origin) {
        Account account = createAccount(dto, role, EProvider.SYSTEM);
        eventPublisher.publishEvent(new AccountCreatedEvent(account, origin));
        return genericMapper.toDTO(account, AccountResponse.class);
    }

    private Account createAccount(AccountDTO dto, ERole role, EProvider provider) {
        checkUsernameExists(dto.getUsername());
        checkEmailExists(dto.getEmail());
        Account account = authFactory.createAccount(dto, provider);
        account = accountRepository.save(account);
        accountRoleService.create(account, role);
        return account;
    }

    public void verifyOtp(String username, String token) {
        if (!jwtService.validateToken(token)) {
            throw new BadRequestException(EMessage.EXPIRED_INVALID.getMessage());
        }
        Account account = accountLookupService.findAccountByIdentifier(username);
        String otp = jwtService.extract(token);
        if (!otp.equals(account.getOtpCode())) {
            throw new BadRequestException(EMessage.INVALID.format("otp"));
        }
        account.setOtpCode(null);
        account.setStatus(EStatus.ACTIVE.getValue());
        accountRepository.save(account);
    }

    public void refreshOtp(String username) {
        Account account = accountLookupService.findAccountByIdentifier(username);
        String token = Libs.generateOtp();
        account.setOtpCode(token);
        accountRepository.save(account);
    }

    private List<String> getRoles(Account account) {
        return accountRoleRepository.findByAccount((account))
                .stream()
                .map(ar -> ar.getRole().getName())
                .distinct()
                .toList();
    }

    public LoginResponse loginUser(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        Account account = accountLookupService.findAccountByIdentifier(loginRequest.getIdentifier());
        if (account.getStatus() != EStatus.ACTIVE.getValue()) {
            throw new AuthException(HttpStatus.FORBIDDEN, "Account FORBIDDEN");
        }
        if (!BCrypt.checkpw(loginRequest.getPassword(), account.getPassword())) {
            throw new AuthException("Invalid password!");
        }
        List<String> roles = getRoles(account);
        String agent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();


        String token = jwtService.generateToken(loginRequest.getIdentifier());
        String refreshToken = jwtService.generateRefreshToken(loginRequest.getIdentifier());

        multiAgentService.createOrUpdateSession(account, agent, ip, token, refreshToken);
        createAndAddTokenCookies(response, token, refreshToken);

        return LoginResponse.builder()
                .username(account.getUsername())
                .roles(roles)
                .build();
    }

    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException(EMessage.REFRESH_TOKEN_REQUIRED.getMessage());
        }
        multiAgentService.clearRefreshToken(refreshToken);
    }

    public LoginResponse refreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException(EMessage.REFRESH_TOKEN_REQUIRED.getMessage());
        }
        if (!jwtService.validateToken(refreshToken)) {
            throw new BadRequestException(EMessage.INVALID.getMessage());
        }

        String identifier = jwtService.extract(refreshToken);

        String newToken = jwtService.generateToken(identifier);
        String newRefreshToken = jwtService.generateRefreshToken(identifier);

        multiAgentService.updateSession(newToken, newRefreshToken);

        Account account = accountLookupService.findAccountByIdentifier(identifier);
        List<String> roles = getRoles(account);
        createAndAddTokenCookies(response, newToken, newRefreshToken);

        return LoginResponse.builder()
                .username(account.getUsername())
                .roles(roles)
                .build();
    }

    public AccountResponse getAccount(String username) {
        Account account = accountLookupService.findAccountByIdentifier(username);
        return genericMapper.toDTO(account, AccountResponse.class);
    }

    public Page<AccountResponse> getAllRelationAccounts(String username, Pageable pageable) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(EMessage.NOT_FOUND.format("username", username)));

        Page<AccountRelation> relations =
                relationRepository.findAllByParentId(account.getId(), pageable);

        return relations.map(rel -> genericMapper.toDTO(rel.getChild(), AccountResponse.class));
    }

    @Transactional
    public AccountResponse updateAccount(String username, UpdateRequest accountDTO) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(EMessage.NOT_FOUND.format("username", username)));
        account = genericUpdater.patch(accountDTO, account, "id", "username");
        account = accountRepository.save(account);
        return genericMapper.toDTO(account, AccountResponse.class);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(EMessage.NOT_FOUND.format("username", username)));

        if (!BCrypt.checkpw(oldPassword, account.getPassword())) {
            throw new AuthException("Old password is incorrect!");
        }

        account.setPassword(Libs.hashPassword(newPassword));
        accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(EMessage.NOT_FOUND.format("username", username)));

        accountRepository.delete(account);
    }

    // ===========================
    // PRIVATE HELPERS
    // ===========================

    private void checkUsernameExists(String username) {
        if (accountRepository.existsByUsername(username)) {
            throw new AlreadyExistsException(
                    String.format("Username '%s' already exists!", username)
            );
        }
    }

    private void checkEmailExists(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new AlreadyExistsException(
                    String.format("Email '%s' already exists!", email)
            );
        }
    }

    private void createCookieHeaders(HttpServletResponse response, ResponseCookie... cookies) {
        for (ResponseCookie cookie : cookies) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
    }

    private void createAndAddTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessTokenCookie = cookieService.createCookie("access_token", accessToken, jwtProperties.expiration() / 1000);
        ResponseCookie refreshTokenCookie = cookieService.createCookie("refresh_token", refreshToken, jwtProperties.refreshExpiration() / 1000);
        createCookieHeaders(response, accessTokenCookie, refreshTokenCookie);
    }

}
