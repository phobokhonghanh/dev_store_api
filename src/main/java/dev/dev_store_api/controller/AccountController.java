package dev.dev_store_api.controller;

import dev.dev_store_api.factory.ResponseFactory;
import dev.dev_store_api.model.dto.request.UpdateRequest;
import dev.dev_store_api.model.dto.response.AccountResponse;
import dev.dev_store_api.model.dto.response.BaseResponse;
import dev.dev_store_api.model.type.EMessage;
import dev.dev_store_api.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // Lấy thông tin account theo username
    @GetMapping("/info")
    public ResponseEntity<BaseResponse<AccountResponse>> information(@RequestParam String username) {
        AccountResponse result = accountService.getAccount(username);
        return ResponseFactory.success(result, EMessage.SUCCESS, HttpStatus.OK);
    }

    // Cập nhật thông tin cá nhân (email, tên, số điện thoại, ...)
    @PutMapping("/update")
    public ResponseEntity<BaseResponse<AccountResponse>> updateAccount(
            @RequestParam String username,
            @Valid @RequestBody UpdateRequest accountDTO
    ) {
        AccountResponse result = accountService.updateAccount(username, accountDTO);
        return ResponseFactory.success(result, EMessage.UPDATED, HttpStatus.OK);
    }

    // Đổi mật khẩu
    @PutMapping("/change-password")
    public ResponseEntity<BaseResponse<Void>> changePassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        accountService.changePassword(username, oldPassword, newPassword);
        return ResponseFactory.success(null, EMessage.UPDATED, HttpStatus.OK);
    }

    // Xóa account
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<Void>> deleteAccount(@RequestParam String username) {
        accountService.deleteAccount(username);
        return ResponseFactory.success(null, EMessage.DELETED, HttpStatus.OK);
    }

    // Lấy danh sách tất cả sub account
    @GetMapping("/relation-account")
    public ResponseEntity<BaseResponse<Page<AccountResponse>>> getAllRelationAccounts(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountResponse> result = accountService.getAllRelationAccounts(username, pageable);
        return ResponseFactory.success(result, EMessage.SUCCESS, HttpStatus.OK);
    }

}
