package org.leovegas.wallet.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NonUniqueTransactionException extends RuntimeException {

    public NonUniqueTransactionException(String message) {
        super(message);
    }
}
