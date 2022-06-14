package org.leovegas.wallet.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserCreditResponse extends TransactionResponse{

    public UserCreditResponse(BigDecimal amount) {
        super(amount);
    }
}
