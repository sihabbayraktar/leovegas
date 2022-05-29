package org.leovegas.wallet.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BalanceInsufficientException extends Exception {

    public BalanceInsufficientException(String message) {
        super(message);
    }

}
