package domain;

import enums.TransactionType;

import java.time.Instant;

public record Transaction(
        String transactionId,
        String fromAccountId,
        String toAccountId,
        Money amount,
        TransactionType type,
        Instant timestamp
) {
}
