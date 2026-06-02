package com.mine.built.it.idempotencychapter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IdempotencyResult {

    private final boolean duplicate;
    private final String  cachedJson;           // non-null only when duplicate = true
    private final IdempotencyKeyEntity entity;  // always present — use in markCompleted

    public static IdempotencyResult cached(String json, IdempotencyKeyEntity entity) {
        return new IdempotencyResult(true, json, entity);
    }

    public static IdempotencyResult proceed(IdempotencyKeyEntity entity) {
        return new IdempotencyResult(false, null, entity);
    }
}
