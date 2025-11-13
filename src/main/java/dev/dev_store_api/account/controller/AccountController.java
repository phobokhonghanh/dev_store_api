package dev.dev_store_api.account.controller;

import dev.dev_store_api.account.config.routes.AccountRoutes;
import dev.dev_store_api.account.dto.AccountResponse;
import dev.dev_store_api.account.dto.UpdateRequest;
import dev.dev_store_api.account.service.AccountServiceImpl;
import dev.dev_store_api.common.dto.BaseResponse;
import dev.dev_store_api.common.factory.ResponseFactory;
import dev.dev_store_api.common.model.type.EMessage;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.context}" + AccountRoutes.PREFIX)
public class AccountController {

    private final AccountServiceImpl accountServiceImpl;

    public AccountController(AccountServiceImpl accountServiceImpl) {
        this.accountServiceImpl = accountServiceImpl;
    }

    @GetMapping(AccountRoutes.INFO)
    public ResponseEntity<BaseResponse<AccountResponse>> information(@RequestParam String username) {
        AccountResponse result = accountServiceImpl.getAccount(username);
        return ResponseFactory.success(result, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    @PutMapping(AccountRoutes.UPDATE)
    public ResponseEntity<BaseResponse<AccountResponse>> updateAccount(
            @RequestParam String username,
            @Valid @RequestBody UpdateRequest accountDTO
    ) {
        AccountResponse result = accountServiceImpl.updateAccount(username, accountDTO);
        return ResponseFactory.success(result, EMessage.UPDATED.getMessage(), HttpStatus.OK);
    }

    @PutMapping(AccountRoutes.CHANGE_PASSWORD)
    public ResponseEntity<BaseResponse<Void>> changePassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        accountServiceImpl.changePassword(username, oldPassword, newPassword);
        return ResponseFactory.success(null, EMessage.UPDATED.getMessage(), HttpStatus.OK);
    }

    @DeleteMapping(AccountRoutes.DELETE)
    public ResponseEntity<BaseResponse<Void>> deleteAccount(@RequestParam String username) {
        accountServiceImpl.deleteAccount(username);
        return ResponseFactory.success(null, EMessage.DELETED.getMessage(), HttpStatus.OK);
    }

    @GetMapping(AccountRoutes.RELATION_ACCOUNT)
    public ResponseEntity<BaseResponse<Page<AccountResponse>>> getAllRelationAccounts(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountResponse> result = accountServiceImpl.getAllRelationAccounts(username, pageable);
        return ResponseFactory.success(result, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

}
