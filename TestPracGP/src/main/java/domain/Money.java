package domain;


import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {

        private static final int SCALE = 2;

        private final BigDecimal amount;

        public Money(BigDecimal amount) {
            this.amount = amount.setScale(SCALE, RoundingMode.HALF_EVEN);
        }

        public static Money of(String value) {
            return new Money(new BigDecimal(value));
        }

        public BigDecimal value() {
            return amount;
        }

        public Money add(Money other) {
            return new Money(amount.add(other.amount));
        }

        public Money subtract(Money other) {
            return new Money(amount.subtract(other.amount));
        }

        public boolean isNegativeOrZero() {
            return amount.compareTo(BigDecimal.ZERO) <= 0;
        }

        public boolean isLessThan(Money other) {
            return amount.compareTo(other.amount) < 0;
        }

        @Override
        public String toString() {
            return amount.toString();
        }

        public BigDecimal getAmount() {
            return amount.setScale(SCALE, RoundingMode.HALF_EVEN);
        }
    }

