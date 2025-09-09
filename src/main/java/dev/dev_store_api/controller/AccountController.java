package dev.dev_store_api.controller;

import dev.dev_store_api.libs.utils.exception.BaseException;
import dev.dev_store_api.libs.utils.exception.NotFoundException;
import dev.dev_store_api.model.dto.AccountResponseDTO;
import dev.dev_store_api.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client-account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    @GetMapping("/information")
    public ResponseEntity<?> information(@RequestParam String username) {
        try {
            AccountResponseDTO account = accountService.getAccount(username);
            return ResponseEntity.ok(account);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
    }
}
