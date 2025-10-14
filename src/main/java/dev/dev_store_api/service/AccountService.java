package dev.dev_store_api.service;

import dev.dev_store_api.libs.utils.GenericMapper;
import dev.dev_store_api.libs.utils.exception.AlreadyExistsException;
import dev.dev_store_api.libs.utils.exception.AuthException;
import dev.dev_store_api.libs.utils.exception.BadRequestException;
import dev.dev_store_api.libs.utils.exception.NotFoundException;
import dev.dev_store_api.model.Account;
import dev.dev_store_api.model.AccountRelation;
import dev.dev_store_api.model.MultiAgent;
import dev.dev_store_api.model.dto.AccountDTO;
import dev.dev_store_api.model.dto.request.LoginRequest;
import dev.dev_store_api.model.dto.request.UpdateRequest;
import dev.dev_store_api.model.dto.response.AccountResponse;
import dev.dev_store_api.model.dto.response.LoginResponse;
import dev.dev_store_api.model.type.EProvider;
import dev.dev_store_api.model.type.EStatus;
import dev.dev_store_api.repository.AccountRelationRepository;
import dev.dev_store_api.repository.AccountRepository;
import dev.dev_store_api.repository.MultiAgentRepository;
import dev.dev_store_api.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.security.SecureRandom;
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountRelationRepository relationRepository;
    private final GenericMapper genericMapper;
    private final AccountRoleService accountRoleService;
    private final JwtService jwtService;
    private final MultiAgentRepository multiAgentRepository;
    private static final SecureRandom random = new SecureRandom();

    public AccountService(AccountRepository accountRepository,
                          AccountRelationRepository relationRepository,
                          GenericMapper genericMapper,
                          AccountRoleService accountRoleService, JwtService jwtService, MultiAgentRepository multiAgentRepository) {
        this.accountRepository = accountRepository;
        this.relationRepository = relationRepository;
        this.genericMapper = genericMapper;
        this.accountRoleService = accountRoleService;
        this.jwtService = jwtService;
        this.multiAgentRepository = multiAgentRepository;
    }

    public Account findAccountByIdentifier(String identifier) {
        return Stream.<Supplier<Optional<Account>>>of(
                        () -> accountRepository.findByUsername(identifier),
                        () -> accountRepository.findByEmail(identifier)
                        // () -> accountRepository.findByPhone(identifier),
                        // () -> accountRepository.findBySsoId(identifier)
                )
                .map(Supplier::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User not found or inactive!"));
    }

    @Transactional
    public AccountResponse createAccount(AccountDTO accountDTO, String role, EProvider provider) {
        try {
            checkUsernameExists(accountDTO.getUsername());
            checkEmailExists(accountDTO.getEmail());
            Account account = genericMapper.toEntity(accountDTO, Account.class);
            account.setPassword(hashPassword(accountDTO.getPassword()));
            account.setStatus(EStatus.UNACTIVE.getValue());
            account.setAuthProvider(provider);
            String token = generateOtp();
            account.setOtpCode(token);
            account = accountRepository.save(account);
            accountRoleService.create(account, role);

            return genericMapper.toDTO(account, AccountResponse.class);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid account data provided.");
        }
    }
    public void verifyOtp(String username, String otp){
        Account account = findAccountByIdentifier(username);
            if(!otp.equals(account.getOtpCode())){
                throw new BadRequestException("Invalid otp");
            }
        account.setOtpCode(null);
        account.setStatus(EStatus.ACTIVE.getValue());
        accountRepository.save(account);
    }
    public void refreshOtp(String username){
        Account account = findAccountByIdentifier(username);
        String token = generateOtp();
        account.setOtpCode(token);
        accountRepository.save(account);
    }
    public LoginResponse validateUser(LoginRequest loginRequest, HttpServletRequest request) {
        Account account = findAccountByIdentifier(loginRequest.getIdentifier());
        if(account.getStatus() != EStatus.ACTIVE.getValue()) {
            throw new AuthException(HttpStatus.FORBIDDEN, "Account FORBIDDEN");
        }
        if (!BCrypt.checkpw(loginRequest.getPassword(), account.getPassword())) {
            throw new AuthException("Invalid password!");
        }
        String agent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();

        Optional<MultiAgent> existingSessionOpt =
                multiAgentRepository.findByAccountAndAgent(account, agent);
        MultiAgent session;

        String token = jwtService.generateToken(loginRequest.getIdentifier());
        String refreshToken = jwtService.generateRefreshToken(loginRequest.getIdentifier());

        if (existingSessionOpt.isPresent()) {
            session = existingSessionOpt.get();

            if (!session.getIsActive()) {
                throw new AuthException("This device has been deactivated. Please contact support.");
            }
            session.setToken(token);
            session.setRefreshToken(refreshToken);
        } else {
            session = new MultiAgent();
            session.setAccount(account);
            session.setAgent(agent);
            session.setIpAddress(ip);
            session.setToken(token);
            session.setRefreshToken(refreshToken);
            session.setIsActive(true);
        }
        multiAgentRepository.save(session);

        return LoginResponse.builder()
                .username(account.getUsername())
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public String logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token is required.");
        }
        MultiAgent session = multiAgentRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() ->
                        new BadRequestException("Logout failed: Session not found or already revoked.")
                );
        session.setRefreshToken(null);
        multiAgentRepository.save(session);
        return "LOGOUT_SUCCESSFUL";
    }

    public LoginResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token is required.");
        }
        if (!jwtService.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid Refresh Token.");
        }

        MultiAgent session = multiAgentRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadRequestException("Refresh Token not found or revoked. Please re-login."));

        String identifier = jwtService.extract(refreshToken);

        String newToken = jwtService.generateToken(identifier);
        String newRefreshToken = jwtService.generateRefreshToken(identifier);

        session.setToken(newToken);
        session.setRefreshToken(newRefreshToken);
        multiAgentRepository.save(session);

        Account account = findAccountByIdentifier(identifier);

        return LoginResponse.builder()
                .username(account.getUsername())
                .token(newToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public AccountResponse getAccount(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Account with username '%s' not found!", username)
                ));
        return genericMapper.toDTO(account, AccountResponse.class);
    }

    public Page<AccountResponse> getAllRelationAccounts(String username, Pageable pageable) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Page<AccountRelation> relations =
                relationRepository.findAllByParentId(account.getId(), pageable);

        return relations.map(rel -> genericMapper.toDTO(rel.getChild(), AccountResponse.class));
    }

    @Transactional
    public AccountResponse updateAccount(String username, UpdateRequest accountDTO) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        BeanUtils.copyProperties(accountDTO, account, getNullPropertyNames(accountDTO));

        account = accountRepository.save(account);
        return genericMapper.toDTO(account, AccountResponse.class);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        if (!BCrypt.checkpw(oldPassword, account.getPassword())) {
            throw new AuthException("Old password is incorrect!");
        }

        account.setPassword(hashPassword(newPassword));
        accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

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

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
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

    public static String generateOtp() {
        int number = random.nextInt(1_000_000);
        return String.format("%06d", number);
    }
}
