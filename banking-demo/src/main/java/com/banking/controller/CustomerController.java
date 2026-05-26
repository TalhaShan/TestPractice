package com.banking.controller;

import com.banking.dto.BankingDtos.*;
import com.banking.service.AccountService;
import com.banking.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Customer Controller.
 *
 * URL Pattern: /api/v1/customers (plural noun, versioned prefix).
 * Sub-resource: /api/v1/customers/{id}/accounts
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final AccountService accountService;

    /**
     * POST /api/v1/customers
     * Create a new customer. Returns 201 Created.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        CustomerResponse customer = customerService.createCustomer(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created", customer));
    }

    /**
     * GET /api/v1/customers/{customerId}
     * Retrieve a customer by ID.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                ApiResponse.success(customerService.getCustomer(customerId)));
    }

    /**
     * GET /api/v1/customers
     * List all customers.
     * NOTE: In production this would be paginated.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.success(customerService.getAllCustomers()));
    }

    /**
     * PUT /api/v1/customers/{customerId}
     * Full update of a customer resource.
     * Use PATCH for partial updates.
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateCustomerRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Customer updated",
                        customerService.updateCustomer(customerId, request)));
    }

    // ── Sub-resource: /customers/{id}/accounts ───────────────────────────────

    /**
     * POST /api/v1/customers/{customerId}/accounts
     * Create an account for a customer (sub-resource creation).
     */
    @PostMapping("/{customerId}/accounts")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateAccountRequest request) {

        // Set customer ID from path (path takes precedence over body)
        request.setCustomerId(customerId);
        AccountResponse account = accountService.createAccount(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created", account));
    }

    /**
     * GET /api/v1/customers/{customerId}/accounts
     * List all accounts for a customer.
     */
    @GetMapping("/{customerId}/accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getCustomerAccounts(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                ApiResponse.success(accountService.getAccountsByCustomer(customerId)));
    }

    /**
     * GET /api/v1/customers/{customerId}/accounts/balance
     * Get total balance across all active accounts.
     */
    @GetMapping("/{customerId}/accounts/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalBalance(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                ApiResponse.success(accountService.getTotalBalance(customerId)));
    }
}
