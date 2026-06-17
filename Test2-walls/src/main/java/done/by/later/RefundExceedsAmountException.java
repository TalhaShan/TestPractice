package done.by.later;

public class RefundExceedsAmountException extends RuntimeException {
    public RefundExceedsAmountException(String s) {
        super(s);
    }
}
