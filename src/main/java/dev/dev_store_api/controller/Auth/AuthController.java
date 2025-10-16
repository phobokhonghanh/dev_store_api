package dev.dev_store_api.controller.Auth;

import dev.dev_store_api.model.dto.AccountDTO;
import dev.dev_store_api.model.dto.response.AccountResponse;
import dev.dev_store_api.model.dto.request.LoginRequest;
import dev.dev_store_api.model.dto.response.LoginResponse;
import dev.dev_store_api.model.type.EProvider;
import dev.dev_store_api.model.type.ERole;
import dev.dev_store_api.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponse> register(@Valid @RequestBody AccountDTO accountDTO) {
        AccountResponse createdAccount = accountService.createAccount(accountDTO, ERole.USER.name(), EProvider.SYSTEM);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }
    @PostMapping(value = "/register-admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponse> registerAdmin(@Valid @RequestBody AccountDTO accountDTO) {
        AccountResponse createdAccount = accountService.createAccount(accountDTO, ERole.ADMIN.name(), EProvider.SYSTEM);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @GetMapping(value = "/register/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyOtp(@RequestParam String username, @RequestParam String otp) {
        accountService.verifyOtp(username, otp);
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping(value = "/refresh/otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refreshOtp(@RequestParam String username) {
        accountService.refreshOtp(username);
        return ResponseEntity.ok("SUCCESS");
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        LoginResponse loginResponse = accountService.validateUser(loginRequest, request);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping(value="/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String refreshToken) {
        String message = accountService.logout(refreshToken);
        return ResponseEntity.ok(message);
    }

    @PostMapping(value="/refresh/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String refreshToken) {
        LoginResponse loginResponse =  accountService.refreshToken(refreshToken);
        return ResponseEntity.ok(loginResponse);
    }
}
