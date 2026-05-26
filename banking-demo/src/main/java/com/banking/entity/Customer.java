package com.banking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer entity.
 *
 * JPA Concepts Demonstrated:
 * - @OneToMany with mappedBy (bidirectional relationship)
 * - CascadeType.ALL: operations cascade to accounts
 * - orphanRemoval: deleting account from list removes it from DB
 * - FetchType.LAZY: accounts not loaded until accessed (N+1 aware)
 * - @CreationTimestamp / @UpdateTimestamp: audit fields auto-populated
 */
@Entity
@Table(name = "customers",
       indexes = {
           @Index(name = "idx_customer_email", columnList = "email", unique = true),
           @Index(name = "idx_customer_national_id", columnList = "national_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "national_id", unique = true, length = 50)
    private String nationalId;

    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private CustomerStatus status = CustomerStatus.ACTIVE;

    /**
     * BIDIRECTIONAL ONE-TO-MANY:
     * - mappedBy = "customer" means Account owns the FK
     * - CascadeType.ALL: save/delete customer => save/delete accounts
     * - orphanRemoval: remove account from list => delete from DB
     * - FetchType.LAZY: ALWAYS prefer LAZY on collections to avoid N+1
     */
    @OneToMany(mappedBy = "customer",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Helper method to maintain bidirectional consistency ──────────────────
    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setCustomer(null);
    }

    public enum CustomerStatus { ACTIVE, SUSPENDED, CLOSED }
}
