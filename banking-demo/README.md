# 🏦 Banking Demo — Spring Boot Interview Reference

## Quick Start
```bash
mvn spring-boot:run
# H2 Console: http://localhost:8080/h2-console  (JDBC: jdbc:h2:mem:bankingdb)
# API Base:   http://localhost:8080/api/v1
```

## Sample API Calls

```bash
# 1. Create customer
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Doe","email":"jane@bank.com"}'

# 2. Create account (customerId=1 from seed data)
curl -X POST http://localhost:8080/api/v1/customers/1/accounts \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"accountType":"CHECKING","initialDeposit":1000}'

# 3. Transfer (with idempotency key)
curl -X POST http://localhost:8080/api/v1/transactions/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "idempotencyKey": "550e8400-e29b-41d4-a716-446655440000",
    "sourceAccountNumber": "ACC-ALICE-001",
    "targetAccountNumber": "ACC-BOB-001",
    "amount": 100.00,
    "description": "Rent payment"
  }'

# 4. Retry same transfer — SAFE, returns original result
curl -X POST http://localhost:8080/api/v1/transactions/transfers \
  -H "Content-Type: application/json" \
  -d '{"idempotencyKey":"550e8400-e29b-41d4-a716-446655440000","sourceAccountNumber":"ACC-ALICE-001","targetAccountNumber":"ACC-BOB-001","amount":100.00}'

# 5. Transaction history (paginated)
curl "http://localhost:8080/api/v1/accounts/1/transactions?page=0&size=10&sort=createdAt,desc"
```

---

## 🎯 Interview Q&A

### CONCURRENCY

**Q: What is the difference between Optimistic and Pessimistic Locking?**

| | Optimistic | Pessimistic |
|---|---|---|
| Mechanism | `@Version` — version check at commit | `SELECT ... FOR UPDATE` — DB row lock |
| DB lock held? | No | Yes, until transaction ends |
| Best for | Read-heavy, low contention | Write-heavy, high contention |
| Conflict handling | `OptimisticLockException` → retry | Other threads block/wait |
| Spring annotation | `@Version` on entity field | `@Lock(PESSIMISTIC_WRITE)` on repo method |
| Risk | Lost update if not retried | Deadlock, reduced throughput |

**Q: How do you prevent deadlocks in concurrent transfers?**
> Always acquire locks in a **canonical order** (e.g., by account ID ascending).
> Transfer A→B and B→A both lock the lower ID first, so they can never wait on each other in a cycle.

**Q: What is `@Retryable` and when would you use it?**
> `@Retryable` from Spring Retry automatically re-invokes a method on specified exceptions.
> We use it on `transfer()` to retry on `ObjectOptimisticLockingFailureException` — up to 3 times
> with exponential backoff (100ms, 200ms, 400ms). Requires `@EnableRetry` on a config class.

**Q: What is `@Transactional(readOnly = true)` and why use it?**
> Tells Hibernate to skip dirty checking and automatic flush on read-only operations.
> Benefits: (1) performance — no overhead of change detection, (2) routing — Spring can
> send queries to a read replica in replicated DB setups.

**Q: What transaction isolation levels exist?**
> - READ_UNCOMMITTED: can read uncommitted ("dirty") data — rarely used
> - READ_COMMITTED (PostgreSQL default): only reads committed data
> - REPEATABLE_READ: same row read twice returns same value within a transaction
> - SERIALIZABLE: highest isolation, transactions appear sequential — slowest

---

### IDEMPOTENCY

**Q: What is idempotency and why does it matter in banking?**
> An operation is idempotent if performing it multiple times has the same effect as once.
> In banking: if a `POST /transfer` request times out, the client retries. Without idempotency,
> the money gets transferred twice. With an idempotency key:
> 1. Client generates UUID before sending the request
> 2. Server checks if key exists → if yes, returns cached response
> 3. DB unique constraint is the safety net for concurrent duplicates

**Q: Why save the Transaction BEFORE modifying balances?**
> So the idempotency key is committed to DB first. If two concurrent requests race past
> the in-memory check, the second INSERT fails with a `DataIntegrityViolationException`
> (unique constraint on `idempotency_key`), which we catch to return the first result.
> This "write-first" pattern is called **optimistic idempotency**.

**Q: Should the idempotency key be in the header or request body?**
> Both are valid. Stripe uses `Idempotency-Key` header. Including it in the body is simpler
> and self-documenting. Headers are better when multiple services need to forward it
> transparently (correlation IDs in distributed tracing).

---

### JPA RELATIONSHIPS

**Q: What is the N+1 problem and how do you fix it?**
> If you fetch 10 accounts and then access `account.getCustomer()` for each, Hibernate
> fires 10 additional SELECTs (one per customer). Total: 11 queries.
>
> Fixes:
> 1. `JOIN FETCH` in JPQL: `SELECT a FROM Account a JOIN FETCH a.customer`
> 2. `@EntityGraph(attributePaths = {"customer"})` on repository method
> 3. `FetchType.EAGER` on the field — but dangerous (loads everywhere, use sparingly)
> 4. Batch fetching: `@BatchSize(size=20)` — N/20 + 1 queries

**Q: What is the difference between `CascadeType.ALL` and `orphanRemoval=true`?**
> - `CascadeType.ALL`: operations (persist, merge, remove, refresh, detach) cascade to children.
>   Saving the parent saves children. Deleting the parent deletes children.
> - `orphanRemoval=true`: if you **remove a child from the parent's collection** in Java,
>   Hibernate will DELETE that child from the DB. Without it, removing from collection just
>   breaks the relationship but leaves the orphan row.

**Q: When would you use `@ManyToMany`?**
> When both sides can have multiple of the other (e.g., accounts having multiple users/signatories,
> users having multiple roles). Creates a join table. Best practice:
> - Use an explicit join entity (e.g., `AccountUser`) for the join table
> - Gives you room to add extra columns (e.g., `addedAt`, `role`)
> - Avoids Hibernate's default join table which you can't enrich

---

### REST API NAMING

| Action | ✅ RESTful | ❌ RPC-style |
|---|---|---|
| Create transfer | `POST /transactions/transfers` | `POST /doTransfer` |
| Get account | `GET /accounts/{number}` | `GET /getAccount?num=X` |
| Freeze account | `PATCH /accounts/{num}/freeze` | `POST /freezeAccount` |
| List transactions | `GET /accounts/{id}/transactions` | `GET /getTransactionsByAccount` |
| Full update | `PUT /customers/{id}` | `POST /updateCustomer` |
| Partial update | `PATCH /customers/{id}` | `POST /updateEmail` |

**Q: When do you use POST vs PUT vs PATCH?**
> - `POST`: create a new resource (server assigns ID). Non-idempotent.
> - `PUT`: replace the entire resource (client provides full representation). Idempotent.
> - `PATCH`: partial update (only changed fields). Idempotent if well-designed.

---

### SPRING / AOP

**Q: How does `@Transactional` work internally?**
> Spring creates a **proxy** around your bean at startup. When you call a `@Transactional`
> method, the proxy intercepts the call, begins a transaction, invokes your method, then
> commits or rolls back. This is AOP in action.
>
> ⚠️ **Common gotcha**: self-invocation bypasses the proxy!
> If method A calls `this.methodB()` and B is `@Transactional`, the transaction annotation
> is IGNORED because you're calling the real object, not the proxy.
> Fix: inject the bean itself, or restructure into separate services.

**Q: What is `@ControllerAdvice` and why is it better than try/catch?**
> `@ControllerAdvice` + `@ExceptionHandler` centralizes exception handling across all controllers.
> Benefits:
> 1. No try/catch boilerplate in every endpoint
> 2. Consistent error response format
> 3. Correct HTTP status codes
> 4. Proper logging level (WARN for business errors, ERROR for system errors)
> 5. Don't leak stack traces or internal details to API consumers

**Q: What are the AOP advice types?**
> - `@Before`: runs before the method
> - `@After`: runs after (always, like finally)
> - `@AfterReturning`: only on successful return
> - `@AfterThrowing`: only when exception is thrown
> - `@Around`: wraps the call — most powerful, can modify args/return/exceptions

---

### GENERAL BEST PRACTICES

**Q: Why use DTOs instead of exposing entities directly?**
> 1. Entities may have lazy-loaded collections → `LazyInitializationException` in JSON serializer
> 2. Entities have bidirectional relationships → `StackOverflowError` from circular JSON
> 3. Entities expose internal DB structure (IDs, versions, timestamps)
> 4. DTOs let you version API separately from DB schema
> 5. DTOs can flatten nested structures for client convenience

**Q: Why `BigDecimal` instead of `double` for money?**
> `double` uses binary floating point. `0.1 + 0.2 = 0.30000000000000004` in binary.
> For money: always use `BigDecimal` with explicit `scale` (decimal places) and
> `RoundingMode`. In JPA: `precision=19, scale=4` stores up to 999,999,999,999,999.9999.

**Q: What does `@Index` on `@Table` do?**
> Creates a database index on the specified column(s). Dramatically speeds up:
> - `WHERE email = ?` — with `unique=true`, also enforces uniqueness
> - `WHERE customer_id = ?` — for all foreign key lookups
> - `ORDER BY created_at` — for sorted queries
> Without indexes, DB does a full table scan. With millions of rows, this is the difference
> between 1ms and 30 seconds.

---

## Project Structure
```
src/main/java/com/banking/
├── BankingApplication.java          # @SpringBootApplication entry point
├── entity/
│   ├── Customer.java                # @OneToMany, audit timestamps
│   ├── Account.java                 # @Version (optimistic lock), @ManyToOne
│   └── Transaction.java            # Idempotency key, immutable fields
├── repository/
│   ├── AccountRepository.java       # @Lock, @EntityGraph, JPQL, native query
│   ├── TransactionRepository.java   # Pagination, idempotency lookup
│   └── CustomerRepository.java      # JOIN FETCH
├── service/
│   ├── TransactionService.java      # Concurrency + Idempotency logic
│   ├── AccountService.java          # @Cacheable, @CacheEvict
│   └── CustomerService.java         # Business validation
├── controller/
│   ├── TransactionController.java   # REST naming, pagination, HTTP codes
│   ├── CustomerController.java      # Sub-resources, versioning
│   └── AccountController.java       # PATCH for partial updates
├── dto/
│   └── BankingDtos.java             # Request/Response DTOs, ApiResponse<T>
├── exception/
│   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   ├── BankingException.java
│   └── ResourceNotFoundException.java
├── aspect/
│   └── BankingAuditAspect.java      # AOP: @Around, @AfterThrowing, timing
└── config/
    ├── AppConfig.java               # @EnableRetry, data seeding
    └── SecurityConfig.java          # Stateless REST security
```
