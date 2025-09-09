package dev.dev_store_api.controller.Auth;

import dev.dev_store_api.libs.utils.exception.AlreadyExistsException;
import dev.dev_store_api.libs.utils.exception.AuthException;
import dev.dev_store_api.libs.utils.exception.NotFoundException;
import dev.dev_store_api.model.dto.AccountDTO;
import dev.dev_store_api.model.dto.AccountResponseDTO;
import dev.dev_store_api.model.dto.request.LoginRequest;
import dev.dev_store_api.model.dto.response.LoginResponse;
import dev.dev_store_api.model.type.ERole;
import dev.dev_store_api.repository.AccountRepository;
import dev.dev_store_api.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping(value ="/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = accountService.validateUser(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (NotFoundException | AuthException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Login failed. Please try again!" + e);
        }
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@Validated @RequestBody AccountDTO accountDTO) {
        try {
            AccountResponseDTO createdAccount = accountService.createAccount(accountDTO, ERole.USER.name());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (AlreadyExistsException e) {
            throw new AlreadyExistsException(e.getMessage());
        } catch (Exception e) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Registration failed. Please try again!");
        }
    }

}
