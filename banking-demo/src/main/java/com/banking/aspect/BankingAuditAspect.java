package com.banking.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Audit & Logging Aspect.
 *
 * CONCEPT: Aspect-Oriented Programming (AOP) for cross-cutting concerns.
 *
 * AOP Terminology:
 * - Aspect: this class — a module of cross-cutting functionality.
 * - Advice: the action taken (@Before, @After, @Around, @AfterThrowing, @AfterReturning).
 * - Pointcut: expression that selects which methods to intercept.
 * - Join Point: the actual execution point (method call).
 * - Weaving: the process of linking aspects to target objects.
 *
 * WHY AOP in interviews?
 * - Avoids polluting business logic with logging/auditing code.
 * - Single place to add timing, tracing, security checks.
 * - Spring uses AOP internally for @Transactional, @Cacheable, @Async.
 *
 * COMMON POINTCUT EXPRESSIONS:
 * - execution(* com.banking.service.*.*(..))  — any method in service package
 * - @annotation(com.banking.aspect.Audited)  — methods annotated with @Audited
 * - within(com.banking.controller.*)         — any join point in controller package
 */
@Aspect
@Component
@Slf4j
public class BankingAuditAspect {

    /**
     * POINTCUT DEFINITION:
     * Matches all public methods in any class in the service package.
     */
    @Pointcut("execution(public * com.banking.service.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(public * com.banking.controller.*.*(..))")
    public void controllerLayer() {}

    /**
     * @Around: wraps the method call — most powerful advice type.
     * Can modify arguments, return value, or suppress exceptions.
     *
     * Used here to:
     * 1. Log method entry with arguments.
     * 2. Measure execution time.
     * 3. Log method exit with result or exception.
     */
    @Around("serviceLayer()")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.debug("[SERVICE] Entering {}.{}() with args: {}",
                className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;

            log.debug("[SERVICE] Exiting {}.{}() in {}ms",
                    className, methodName, elapsed);

            // Warn if method takes too long (potential N+1 or missing index)
            if (elapsed > 500) {
                log.warn("[PERFORMANCE] Slow service call: {}.{}() took {}ms",
                        className, methodName, elapsed);
            }

            return result;

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[SERVICE] Exception in {}.{}() after {}ms: {}",
                    className, methodName, elapsed, e.getMessage());
            throw e; // Always rethrow — don't swallow exceptions in AOP
        }
    }

    /**
     * @AfterThrowing: runs only when the advised method throws an exception.
     * Useful for auditing failed operations without wrapping with @Around.
     */
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "exception")
    public void auditException(JoinPoint joinPoint, Exception exception) {
        log.warn("[AUDIT] Exception in {}.{}: {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception.getMessage());
        // In production: write to audit_log table, send to SIEM, etc.
    }

    /**
     * @AfterReturning: runs only on successful method completion.
     * 'returning' binds the method's return value to a parameter.
     */
    @AfterReturning(pointcut = "controllerLayer()", returning = "result")
    public void logControllerResponse(JoinPoint joinPoint, Object result) {
        log.debug("[CONTROLLER] {}.{} returned: {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                result != null ? result.getClass().getSimpleName() : "null");
    }
}
