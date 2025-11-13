package dev.dev_store_api.auth.controller;

import dev.dev_store_api.account.dto.AccountDTO;
import dev.dev_store_api.account.dto.AccountResponse;
import dev.dev_store_api.account.service.AccountServiceImpl;
import dev.dev_store_api.auth.config.routes.AuthRoutes;
import dev.dev_store_api.auth.dto.LoginRequest;
import dev.dev_store_api.auth.dto.LoginResponse;
import dev.dev_store_api.common.dto.BaseResponse;
import dev.dev_store_api.common.factory.ResponseFactory;
import dev.dev_store_api.common.model.type.EMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("${app.api.context}" + AuthRoutes.PREFIX)
public class AuthController {

    private final AccountServiceImpl accountServiceImpl;

    public AuthController(AccountServiceImpl accountServiceImpl) {
        this.accountServiceImpl = accountServiceImpl;
    }

    @PostMapping(value = AuthRoutes.REGISTER, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<AccountResponse>> registerUser(@Valid @RequestBody AccountDTO dto, @RequestHeader("Origin") String origin) {
        AccountResponse result = accountServiceImpl.registerUser(dto, origin);
        return ResponseFactory.success(result, EMessage.CREATED.getMessage(), HttpStatus.CREATED);
    }

    @PostMapping(value = AuthRoutes.REGISTER_ADMIN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<AccountResponse>> registerAdmin(@Valid @RequestBody AccountDTO accountDTO, @RequestHeader("Origin") String origin) {
        AccountResponse result = accountServiceImpl.registerAdmin(accountDTO, origin);
        return ResponseFactory.success(result, EMessage.CREATED.getMessage(), HttpStatus.CREATED);
    }

    @GetMapping(value = AuthRoutes.VERIFY_OTP)
    public ResponseEntity<Void> verifyOtp(@PathVariable String username, @RequestParam String token, @RequestParam("redirect_url") String redirectUrl) {
        try {
            accountServiceImpl.verifyOtp(username, token);
            String successUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                    .queryParam("activation_status", "success")
                    .toUriString();
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(successUrl)).build();
        } catch (RuntimeException e) {
            String failureUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                    .queryParam("activation_status", "failed")
                    .queryParam("reason", e.getMessage())
                    .toUriString();
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(failureUrl)).build();
        }
    }

    @GetMapping(value = AuthRoutes.REFRESH_OTP, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> refreshOtp(@RequestParam String username) {
        accountServiceImpl.refreshOtp(username);
        return ResponseFactory.success(null, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    @PostMapping(value = AuthRoutes.LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        LoginResponse result = accountServiceImpl.loginUser(loginRequest, request, response);
        return ResponseFactory.success(result, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    @PostMapping(value = AuthRoutes.LOGOUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> logout(@CookieValue(name = "refresh_token") String refreshToken) {
        accountServiceImpl.logout(refreshToken);
        return ResponseFactory.success(null, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    @PostMapping(value = AuthRoutes.REFRESH_TOKEN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<String>> refresh(@CookieValue(name = "refresh_token") String refreshToken, HttpServletResponse response) {
        LoginResponse result = accountServiceImpl.refreshToken(refreshToken, response);
        return ResponseFactory.success(result.getUsername(), EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    @GetMapping(value = AuthRoutes.REGISTRATION_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<String>> getRegistrationStatus(@RequestParam String email) {
        String status = accountServiceImpl.getAccountStatusByEmail(email);
        return ResponseFactory.success(status, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }
}
