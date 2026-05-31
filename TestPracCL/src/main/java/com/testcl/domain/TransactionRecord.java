package com.testcl.domain;

import com.testcl.enums.TransferResult;

import java.time.Instant;
import java.util.UUID;

public record TransactionRecord(
    UUID id,
    String fromAccountId,
    String toAccountId,
    Money amount,
    TransferResult result,
    Instant timestamp
) {}
