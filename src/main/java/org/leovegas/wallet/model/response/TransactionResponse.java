package org.leovegas.wallet.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;


@AllArgsConstructor
@Getter
public class TransactionResponse {

    private BigDecimal balance;
}
