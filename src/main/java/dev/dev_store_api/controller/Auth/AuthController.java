package dev.dev_store_api.controller.Auth;

import dev.dev_store_api.factory.ResponseFactory;
import dev.dev_store_api.model.dto.AccountDTO;
import dev.dev_store_api.model.dto.request.LoginRequest;
import dev.dev_store_api.model.dto.response.AccountResponse;
import dev.dev_store_api.model.dto.response.BaseResponse;
import dev.dev_store_api.model.dto.response.LoginResponse;
import dev.dev_store_api.model.type.EMessage;
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
    public ResponseEntity<BaseResponse<AccountResponse>> registerUser(@Valid @RequestBody AccountDTO dto) {
        AccountResponse result = accountService.registerUser(dto);
        return ResponseFactory.success(result, EMessage.CREATED, HttpStatus.CREATED);
    }

    @PostMapping(value = "/register-admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<AccountResponse>> registerAdmin(@Valid @RequestBody AccountDTO accountDTO) {
        AccountResponse result = accountService.registerAdmin(accountDTO);
        return ResponseFactory.success(result, EMessage.CREATED, HttpStatus.CREATED);
    }

    @GetMapping(value = "/register/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> verifyOtp(@RequestParam String username, @RequestParam String otp) {
        accountService.verifyOtp(username, otp);
        return ResponseFactory.success(null, EMessage.SUCCESS, HttpStatus.OK);
    }

    @GetMapping(value = "/refresh/otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> refreshOtp(@RequestParam String username) {
        accountService.refreshOtp(username);
        return ResponseFactory.success(null, EMessage.SUCCESS, HttpStatus.OK);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        LoginResponse result = accountService.validateUser(loginRequest, request);
        return ResponseFactory.success(result, EMessage.SUCCESS, HttpStatus.OK);
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> logout(@RequestHeader("Authorization") String refreshToken) {
        accountService.logout(refreshToken);
        return ResponseFactory.success(null, EMessage.SUCCESS, HttpStatus.OK);
    }

    @PostMapping(value = "/refresh/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<LoginResponse>> refresh(@RequestHeader("Authorization") String refreshToken) {
        LoginResponse result = accountService.refreshToken(refreshToken);
        return ResponseFactory.success(result, EMessage.SUCCESS, HttpStatus.OK);
    }
}
