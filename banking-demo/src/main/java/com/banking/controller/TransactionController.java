package com.banking.controller;

import com.banking.dto.BankingDtos.*;
import com.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Transaction Controller.
 *
 * REST API BEST PRACTICES DEMONSTRATED:
 *
 * 1. RESOURCE NAMING:
 *    - Nouns, not verbs: /transactions NOT /doTransfer
 *    - Plural nouns: /transactions, /accounts
 *    - Hierarchy: /accounts/{id}/transactions (account-scoped)
 *    - Actions as sub-resources: /transactions/transfers (not /transferMoney)
 *
 * 2. HTTP METHODS:
 *    - POST /transactions/transfers  → create a transfer (returns 201 Created)
 *    - POST /transactions/deposits   → create a deposit  (returns 201 Created)
 *    - GET  /transactions/{id}       → get one           (returns 200 OK)
 *    - GET  /accounts/{id}/transactions → list (paginated, 200 OK)
 *
 * 3. IDEMPOTENCY HEADER vs Body:
 *    - Some APIs use X-Idempotency-Key header (Stripe pattern).
 *    - We include it in the body for simplicity — both are valid in interviews.
 *
 * 4. PAGINATION:
 *    - Always paginate list endpoints: page, size, sort params.
 *    - Return Page<T> which includes total elements, pages, etc.
 *    - Default page size = 20; cap at 100.
 *
 * 5. RESPONSE CODES:
 *    - 201 Created for POST that creates resources (include Location header in production).
 *    - 200 OK for idempotent duplicate requests (same resource returned).
 *    - 409 Conflict for concurrent modification.
 *    - 422 Unprocessable Entity for business rule violations.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * POST /api/v1/transactions/transfers
     * Create a fund transfer.
     *
     * Client must include idempotencyKey in request body.
     * Idempotent: safe to retry on network failure.
     */
    @PostMapping("/transactions/transfers")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransferRequest request) {

        TransactionResponse response = transactionService.transfer(request);

        // 201 Created for first-time; 200 OK for idempotent duplicates
        // In this implementation we return 201 for both (acceptable in interviews).
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transfer processed", response));
    }

    /**
     * POST /api/v1/transactions/deposits
     * Deposit funds into an account.
     */
    @PostMapping("/transactions/deposits")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @Valid @RequestBody DepositRequest request) {

        TransactionResponse response = transactionService.deposit(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deposit processed", response));
    }

    /**
     * GET /api/v1/transactions/{transactionId}
     * Retrieve a specific transaction.
     */
    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(
            @PathVariable Long transactionId) {

        return ResponseEntity.ok(
                ApiResponse.success(transactionService.getTransaction(transactionId)));
    }

    /**
     * GET /api/v1/accounts/{accountId}/transactions?from=...&to=...&page=0&size=20&sort=createdAt,desc
     *
     * Paginated transaction history for an account.
     * Date filtering with ISO 8601 format.
     * Sort by any field.
     */
    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        // Cap page size to prevent abuse
        size = Math.min(size, 100);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Default to last 30 days if not specified
        LocalDateTime effectiveFrom = from != null ? from : LocalDateTime.now().minusDays(30);
        LocalDateTime effectiveTo = to != null ? to : LocalDateTime.now();

        Page<TransactionResponse> history = transactionService
                .getTransactionHistory(accountId, effectiveFrom, effectiveTo, pageable);

        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
