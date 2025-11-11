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
import dev.dev_store_api.common.util.Libs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class AccountServiceImpl implements AccountServices {

    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AccountRelationRepository relationRepository;
    private final GenericMapper genericMapper;
    private final AccountRoleService accountRoleService;
    private final JwtService jwtService;
    private final MultiAgentService multiAgentService;
    private final AuthFactory authFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final CookieService cookieService;
    private final JwtProperties jwtProperties;

    public AccountServiceImpl(AccountRepository accountRepository, AccountRoleRepository accountRoleRepository,
                              AccountRelationRepository relationRepository,
                              GenericMapper genericMapper,
                              AccountRoleService accountRoleService,
                              JwtService jwtService,
                              MultiAgentService multiAgentService,
                              AuthFactory authFactory,
                              ApplicationEventPublisher eventPublisher,
                              CookieService cookieService,
                              JwtProperties jwtProperties) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.relationRepository = relationRepository;
        this.genericMapper = genericMapper;
        this.accountRoleService = accountRoleService;
        this.jwtService = jwtService;
        this.multiAgentService = multiAgentService;
        this.authFactory = authFactory;
        this.eventPublisher = eventPublisher;
        this.cookieService = cookieService;
        this.jwtProperties = jwtProperties;
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
        if (!jwtService.validateToken(token)){
            throw new BadRequestException(EMessage.EXPIRED_INVALID.getMessage());
        }
        Account account = findAccountByIdentifier(username);
        String otp = jwtService.extract(token);
        if (!otp.equals(account.getOtpCode())) {
            throw new BadRequestException(EMessage.INVALID.format("otp"));
        }
        account.setOtpCode(null);
        account.setStatus(EStatus.ACTIVE.getValue());
        accountRepository.save(account);
    }

    public void refreshOtp(String username) {
        Account account = findAccountByIdentifier(username);
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
        Account account = findAccountByIdentifier(loginRequest.getIdentifier());
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

        Account account = findAccountByIdentifier(identifier);
        List<String> roles = getRoles(account);
        createAndAddTokenCookies(response, newToken, newRefreshToken);

        return LoginResponse.builder()
                .username(account.getUsername())
                .roles(roles)
                .build();
    }
    public Account getAccountByToken(String token) {
        String identifier = jwtService.extract(token);
        return findAccountByIdentifier(identifier);
    }

    public AccountResponse getAccount(String username) {
        Account account = findAccountByIdentifier(username);
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

        BeanUtils.copyProperties(accountDTO, account, getNullPropertyNames(accountDTO));

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

    public String getAccountStatusByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(EMessage.NOT_FOUND.format("email", email)));
        return EStatus.values()[account.getStatus()].name();
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

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
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
