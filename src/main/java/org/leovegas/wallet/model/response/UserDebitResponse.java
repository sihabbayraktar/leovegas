package org.leovegas.wallet.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserDebitResponse extends TransactionResponse{

    public UserDebitResponse(BigDecimal amount) {
        super(amount);
    }
}
