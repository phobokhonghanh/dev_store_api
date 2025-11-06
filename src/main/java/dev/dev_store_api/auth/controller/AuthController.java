package dev.dev_store_api.auth.controller;

import dev.dev_store_api.account.dto.AccountDTO;
import dev.dev_store_api.account.dto.AccountResponse;
import dev.dev_store_api.account.service.AccountService;
import dev.dev_store_api.auth.config.routes.AuthRoutes;
import dev.dev_store_api.auth.dto.LoginRequest;
import dev.dev_store_api.auth.dto.LoginResponse;
import dev.dev_store_api.auth.service.CookieService;
import dev.dev_store_api.common.config.properties.JwtProperties;
import dev.dev_store_api.common.dto.BaseResponse;
import dev.dev_store_api.common.factory.ResponseFactory;
import dev.dev_store_api.common.model.type.EMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("${app.api.context}" + AuthRoutes.PREFIX)
public class AuthController {

    private final AccountService accountService;
    private final CookieService cookieService;
    private final JwtProperties jwtProperties;

    public AuthController(AccountService accountService, CookieService cookieService, JwtProperties jwtProperties) {
        this.accountService = accountService;
        this.cookieService = cookieService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping(value = AuthRoutes.REGISTER, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<AccountResponse>> registerUser(@Valid @RequestBody AccountDTO dto, @RequestHeader("Origin") String origin) {
        AccountResponse result = accountService.registerUser(dto, origin);
        return ResponseFactory.success(result, EMessage.CREATED.getMessage(), HttpStatus.CREATED);
    }

    @PostMapping(value = AuthRoutes.REGISTER_ADMIN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<AccountResponse>> registerAdmin(@Valid @RequestBody AccountDTO accountDTO, @RequestHeader("Origin") String origin) {
        AccountResponse result = accountService.registerAdmin(accountDTO, origin);
        return ResponseFactory.success(result, EMessage.CREATED.getMessage(), HttpStatus.CREATED);
    }

    @GetMapping(value = AuthRoutes.VERIFY_OTP)
    public ResponseEntity<Void> verifyOtp(@PathVariable String username, @RequestParam String token, @RequestParam("redirect_url") String redirectUrl) {
        try {
            accountService.verifyOtp(username, token);
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
        accountService.refreshOtp(username);
        return ResponseFactory.success(null, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    @PostMapping(value = AuthRoutes.LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<String>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        LoginResponse result = accountService.validateUser(loginRequest, request);

        HttpHeaders headers = createAndAddTokenCookies(result.getToken(), result.getRefreshToken());

        return ResponseFactory.success(result.getUsername(), EMessage.SUCCESS.getMessage(), HttpStatus.OK, headers);
    }

    @PostMapping(value = AuthRoutes.LOGOUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> logout(@CookieValue(name = "refresh_token") String refreshToken) {
        accountService.logout(refreshToken);
        ResponseCookie deletedAccessToken = cookieService.deleteCookie("access_token");
        ResponseCookie deletedRefreshToken = cookieService.deleteCookie("refresh_token");

        HttpHeaders headers = createCookieHeaders(deletedAccessToken, deletedRefreshToken);

        return ResponseFactory.success(null, EMessage.SUCCESS.getMessage(), HttpStatus.OK, headers);
    }

    @PostMapping(value = AuthRoutes.REFRESH_TOKEN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<String>> refresh(@CookieValue(name = "refresh_token") String refreshToken) {
        LoginResponse result = accountService.refreshToken(refreshToken);

        HttpHeaders headers = createAndAddTokenCookies(result.getToken(), result.getRefreshToken());

        return ResponseFactory.success(result.getUsername(), EMessage.SUCCESS.getMessage(), HttpStatus.OK, headers);
    }

    @GetMapping(value = AuthRoutes.REGISTRATION_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<String>> getRegistrationStatus(@RequestParam String email) {
        String status = accountService.getAccountStatusByEmail(email);
        return ResponseFactory.success(status, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    private HttpHeaders createCookieHeaders(ResponseCookie... cookies) {
        HttpHeaders headers = new HttpHeaders();
        for (ResponseCookie cookie : cookies) {
            headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        return headers;
    }

    private HttpHeaders createAndAddTokenCookies(String accessToken, String refreshToken) {
        ResponseCookie accessTokenCookie = cookieService.createCookie("access_token", accessToken, jwtProperties.expiration() / 1000);
        ResponseCookie refreshTokenCookie = cookieService.createCookie("refresh_token", refreshToken, jwtProperties.refreshExpiration() / 1000);
        return createCookieHeaders(accessTokenCookie, refreshTokenCookie);
    }
}
