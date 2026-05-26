package com.banking.repository;

import com.banking.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Account Repository.
 *
 * CONCEPTS DEMONSTRATED:
 *
 * 1. PESSIMISTIC LOCKING — @Lock(PESSIMISTIC_WRITE):
 *    Generates "SELECT ... FOR UPDATE" in SQL.
 *    The selected row is locked until the transaction commits.
 *    Use when you MUST prevent concurrent modification (e.g., balance transfers).
 *    Downside: other threads block — lower throughput than optimistic locking.
 *
 * 2. JPQL vs Native Query:
 *    - JPQL operates on entity objects (database-agnostic).
 *    - @Query(nativeQuery=true) is raw SQL — faster but DB-specific.
 *
 * 3. Projections (interface-based):
 *    Fetch only what you need — avoids loading the full entity graph.
 *    Useful for summary/list endpoints.
 *
 * 4. @EntityGraph:
 *    Eagerly fetch specified associations in a single JOIN query.
 *    Solves N+1 problem without changing the entity's FetchType.
 *    Better than fetch joins in JPQL for reuse.
 *
 * 5. @Modifying + @Transactional:
 *    Required for UPDATE/DELETE JPQL queries.
 *    @Transactional on repo method is a fallback; prefer service-layer transactions.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // ── Simple Derived Query ─────────────────────────────────────────────────
    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(Long customerId);

    List<Account> findByCustomerIdAndStatus(Long customerId, Account.AccountStatus status);

    // ── PESSIMISTIC WRITE LOCK: SELECT ... FOR UPDATE ────────────────────────
    // Use in transfer operations where we need guaranteed exclusive access.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);

    // ── JPQL with JOIN FETCH (solves N+1 for customer) ───────────────────────
    @Query("SELECT a FROM Account a JOIN FETCH a.customer WHERE a.id = :id")
    Optional<Account> findByIdWithCustomer(@Param("id") Long id);

    // ── @EntityGraph — declarative eager loading ─────────────────────────────
    // Equivalent to JOIN FETCH but reusable & cleaner
    @EntityGraph(attributePaths = {"customer"})
    List<Account> findByStatus(Account.AccountStatus status);

    // ── Aggregate query ──────────────────────────────────────────────────────
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.customer.id = :customerId AND a.status = 'ACTIVE'")
    Optional<BigDecimal> sumBalanceByCustomerId(@Param("customerId") Long customerId);

    // ── Bulk update — @Modifying + @Transactional ────────────────────────────
    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("UPDATE Account a SET a.status = :status WHERE a.customer.id = :customerId")
    int updateStatusByCustomerId(@Param("customerId") Long customerId,
                                 @Param("status") Account.AccountStatus status);

    // ── Native Query example ─────────────────────────────────────────────────
    @Query(value = """
            SELECT a.account_number, a.balance, c.first_name, c.last_name
            FROM accounts a
            INNER JOIN customers c ON a.customer_id = c.id
            WHERE a.balance > :threshold
            ORDER BY a.balance DESC
            """, nativeQuery = true)
    List<Object[]> findHighBalanceAccountsNative(@Param("threshold") BigDecimal threshold);

    boolean existsByAccountNumber(String accountNumber);
}
