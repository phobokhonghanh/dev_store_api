package dev.dev_store_api.account.controller;

import dev.dev_store_api.account.config.routes.AccountRoutes;
import dev.dev_store_api.common.factory.ResponseFactory;
import dev.dev_store_api.account.dto.UpdateRequest;
import dev.dev_store_api.account.dto.AccountResponse;
import dev.dev_store_api.common.dto.BaseResponse;
import dev.dev_store_api.common.model.type.EMessage;
import dev.dev_store_api.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.context}/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(AccountRoutes.INFO)
    public ResponseEntity<BaseResponse<AccountResponse>> information(@RequestParam String username) {
        AccountResponse result = accountService.getAccount(username);
        return ResponseFactory.success(result, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    @PutMapping(AccountRoutes.UPDATE)
    public ResponseEntity<BaseResponse<AccountResponse>> updateAccount(
            @RequestParam String username,
            @Valid @RequestBody UpdateRequest accountDTO
    ) {
        AccountResponse result = accountService.updateAccount(username, accountDTO);
        return ResponseFactory.success(result, EMessage.UPDATED.getMessage(), HttpStatus.OK);
    }

    @PutMapping(AccountRoutes.CHANGE_PASSWORD)
    public ResponseEntity<BaseResponse<Void>> changePassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        accountService.changePassword(username, oldPassword, newPassword);
        return ResponseFactory.success(null, EMessage.UPDATED.getMessage(), HttpStatus.OK);
    }

    @DeleteMapping(AccountRoutes.DELETE)
    public ResponseEntity<BaseResponse<Void>> deleteAccount(@RequestParam String username) {
        accountService.deleteAccount(username);
        return ResponseFactory.success(null, EMessage.DELETED.getMessage(), HttpStatus.OK);
    }

    @GetMapping(AccountRoutes.RELATION_ACCOUNT)
    public ResponseEntity<BaseResponse<Page<AccountResponse>>> getAllRelationAccounts(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountResponse> result = accountService.getAllRelationAccounts(username, pageable);
        return ResponseFactory.success(result, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

}
