package dev.dev_store_api.controller;

import dev.dev_store_api.model.dto.response.AccountResponse;
import dev.dev_store_api.model.dto.request.UpdateRequest;
import dev.dev_store_api.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/client-account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Lấy thông tin account theo username
    @GetMapping("/info")
    public ResponseEntity<AccountResponse> information(@RequestParam String username) {
        return ResponseEntity.ok(accountService.getAccount(username));
    }

    // Cập nhật thông tin cá nhân (email, tên, số điện thoại, ...)
    @PutMapping("/update")
    public ResponseEntity<AccountResponse> updateAccount(
            @RequestParam String username,
            @Valid @RequestBody UpdateRequest accountDTO
    ) {
        AccountResponse updatedAccount = accountService.updateAccount(username, accountDTO);
        return ResponseEntity.ok(updatedAccount);
    }

    // Đổi mật khẩu
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        accountService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully!");
    }

    // Xóa account
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@RequestParam String username) {
        accountService.deleteAccount(username);
        return ResponseEntity.ok("Account deleted successfully!");
    }

    // Lấy danh sách tất cả sub account
    @GetMapping("/relation-account")
    public ResponseEntity<Page<AccountResponse>> getAllRelationAccounts(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(accountService.getAllRelationAccounts(username, pageable));
    }

}
