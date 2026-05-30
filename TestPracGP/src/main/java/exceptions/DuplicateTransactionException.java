package exceptions;

public class DuplicateTransactionException extends RuntimeException {

    public DuplicateTransactionException(String transactionId) {
        super("Duplicate transaction: " + transactionId);
    }
}
