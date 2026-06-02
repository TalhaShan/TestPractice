package com.mine.built.it.idempotencychapter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository repo;

    @Transactional
    public IdempotencyResult handleRequest(String key, String requestHash) {
        Optional<IdempotencyKeyEntity> existing = repo.findById(key);
        // Existing key found
        if (existing.isPresent()) {
            IdempotencyKeyEntity entity = existing.get();
            // Same key + different payload
            if (!entity.getRequestHash().equals(requestHash)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency key reused with different payload"
                );
            }

            // Already completed → return cached response
            if (entity.getStatus() == IdempotencyKeyEntity.Status.COMPLETED) {
                return IdempotencyResult.cached(
                        entity.getResponseJson(),
                        entity
                );
            }

            // Still processing
            if (entity.getStatus() == IdempotencyKeyEntity.Status.IN_PROGRESS) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Request already in progress"
                );
            }
        }

        // First request
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.setKey(key);
        entity.setRequestHash(requestHash);
        entity.setStatus(IdempotencyKeyEntity.Status.IN_PROGRESS);
        repo.save(entity);
        return IdempotencyResult.proceed(entity);
    }

    @Transactional
    public void markCompleted(IdempotencyKeyEntity entity, String responseJson) {

        entity.setStatus(IdempotencyKeyEntity.Status.COMPLETED);
        entity.setResponseJson(responseJson);

        repo.save(entity);
    }

    @Transactional
    public void markFailed(IdempotencyKeyEntity entity) {

        entity.setStatus(IdempotencyKeyEntity.Status.FAILED);

        repo.save(entity);
    }
}
