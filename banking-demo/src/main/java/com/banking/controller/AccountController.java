package com.banking.controller;

import com.banking.dto.BankingDtos.*;
import com.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Account Controller — standalone account-level endpoints.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * GET /api/v1/accounts/{accountNumber}
     * Get account by account number.
     * Note: path variable is accountNumber (string), not DB id.
     * This is good practice — expose business keys, not internal IDs.
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                ApiResponse.success(accountService.getAccount(accountNumber)));
    }

    /**
     * PATCH /api/v1/accounts/{accountNumber}/freeze
     * Freeze an account (partial update, action as sub-resource).
     *
     * PATCH is used for partial updates.
     * "freeze" is an action — acceptable as a sub-resource for state transitions.
     */
    @PatchMapping("/{accountNumber}/freeze")
    public ResponseEntity<ApiResponse<AccountResponse>> freezeAccount(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                ApiResponse.success("Account frozen",
                        accountService.freezeAccount(accountNumber)));
    }
}
