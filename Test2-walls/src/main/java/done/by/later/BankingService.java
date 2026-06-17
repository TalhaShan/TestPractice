package done.by.later;

import com.mine.test2.exceptions.TransactionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BankingService {

    private final RefundRepository refundRepository;
    private final TransactionRepository transactionRepository;


    @Transactional
    public Transaction createTransaction(String name, BigDecimal amount) {

        if(transactionRepository.findByName(name).isPresent()) {
            throw new DuplicateNameException("Transaction name already exists: " + name);
        }

        var tx = new Transaction();
        tx.setName(name);
        tx.setAmount(amount);
        transactionRepository.save(tx);

        return tx;
    }

    @Transactional(readOnly = true)
    public Transaction getTransaction(String name) {
        return transactionRepository.findByName(name).orElseThrow(TransactionNotFoundException::new);
    }


    @Transactional
    public Refund createRefund(String txName, String refundName, BigDecimal amount) {
        //1. Acquired Lock

        var tx  = transactionRepository.findByNameForUpdate(txName).orElseThrow(TransactionNotFoundException::new);

        //2, Idempotency Check
        if(refundRepository.existsByName(refundName)){
            throw new DuplicateNameException("Refund name already exists: " + refundName);
        }

        //3. Check for amount
        BigDecimal alreadyRefundedAmount = refundRepository.sumRefundedAmount(tx);
        BigDecimal newTotal = alreadyRefundedAmount.add(amount);
        if(newTotal.compareTo(tx.getAmount()) > 0) {
            //Refunds exceeding the actual amount
            throw new RefundExceedsAmountException(
                    "Refund would exceed transaction amount. " +
                            "Already refunded: " + alreadyRefundedAmount +
                            ", requested: " + amount +
                            ", limit: " + tx.getAmount()
            );
        }

        //4. Persist
        var refund = new Refund();
        refund.setName(refundName);
        refund.setAmount(amount);
        refund.setTransaction(tx);
        return refundRepository.save(refund);
    }

    //Same Response
    @Transactional
    public Refund createRefundReturningSameResponseForIdempotency(String txName, String refundName, BigDecimal amount) {
        var tx = transactionRepository.findByNameForUpdate(txName)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found: " + txName));

        // Idempotency: if this exact refund already exists, return it as-is
        var existing = refundRepository.findByName(refundName);
        if (existing.isPresent()) {
            Refund refund = existing.get();
            // Same transaction + same amount → idempotent success
            if (refund.getTransaction().equals(tx) && refund.getAmount().compareTo(amount) == 0) {
                return refund;
            }
            // Different params → genuine conflict
            throw new ConflictException("Refund name '" + refundName + "' already used with different parameters");
        }

        //3. Check for amount
        BigDecimal alreadyRefundedAmount = refundRepository.sumRefundedAmount(tx);
        BigDecimal newTotal = alreadyRefundedAmount.add(amount);
        if(newTotal.compareTo(tx.getAmount()) > 0) {
            //Refunds exceeding the actual amount
            throw new RefundExceedsAmountException(
                    "Refund would exceed transaction amount. " +
                            "Already refunded: " + alreadyRefundedAmount +
                            ", requested: " + amount +
                            ", limit: " + tx.getAmount()
            );
        }

        //4. Persist
        var refund = new Refund();
        refund.setName(refundName);
        refund.setAmount(amount);
        refund.setTransaction(tx);
        return refundRepository.save(refund);
    }


}
