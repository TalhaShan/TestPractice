package com.mine.built.it.idempotencychapter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "idempotency_key")
@Getter
@Setter
public class IdempotencyKeyEntity {

    @Id
    @Column(name = "idem_key", length = 128)
    private String key;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "request_hash", length = 64)
    private String requestHash;

    @Column(name = "response_json", columnDefinition = "json")
    private String responseJson;

    @CreationTimestamp
    private Instant createdOn;

    @UpdateTimestamp
    private Instant updatedOn;

    // standard setters and getters
    public enum Status {IN_PROGRESS, COMPLETED, FAILED}

}
