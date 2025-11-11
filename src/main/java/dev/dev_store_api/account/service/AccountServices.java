package dev.dev_store_api.account.service;

import dev.dev_store_api.account.dto.AccountDTO;
import dev.dev_store_api.account.dto.AccountResponse;
import dev.dev_store_api.account.dto.UpdateRequest;
import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.auth.dto.LoginRequest;
import dev.dev_store_api.auth.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountServices {
    Account findAccountByIdentifier(String identifier);
    Account getAccountByToken(String token);

    AccountResponse registerUser(AccountDTO dto, String origin);
    AccountResponse registerAdmin(AccountDTO dto, String origin);
    AccountResponse registerWithFacebook(AccountDTO dto);
    AccountResponse registerWithGoogle(AccountDTO dto);
    AccountResponse getAccount(String username);
    AccountResponse updateAccount(String username, UpdateRequest accountDTO);
    Page<AccountResponse> getAllRelationAccounts(String username, Pageable pageable);

    void verifyOtp(String username, String token);
    void refreshOtp(String username);
    void logout(String refreshToken);
    void changePassword(String username, String oldPassword, String newPassword);
    void deleteAccount(String username);

    LoginResponse loginUser(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response);
    LoginResponse refreshToken(String refreshToken, HttpServletResponse response);

    String getAccountStatusByEmail(String email);



}
