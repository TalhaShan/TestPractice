package domain;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;

import java.util.concurrent.locks.ReentrantLock;

public class Account {

        private final String id;

        private Money balance;

        private final ReentrantLock lock = new ReentrantLock();

        public Account(String id, Money initialBalance) {
            this.id = id;
            this.balance = initialBalance;
        }

        public String getId() {
            return id;
        }

        public Money getBalance() {
            return balance;
        }

        public ReentrantLock getLock() {
            return lock;
        }

        public void deposit(Money amount) {

            validate(amount);

            balance = balance.add(amount);
        }

        public void withdraw(Money amount) {

            validate(amount);

            if (balance.isLessThan(amount)) {
                throw new InsufficientFundsException();
            }

            balance = balance.subtract(amount);
        }

        private void validate(Money amount) {
            if (amount.isNegativeOrZero()) {
                throw new InvalidAmountException();
            }
        }

}
