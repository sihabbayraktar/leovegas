package org.leovegas.wallet.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserCreditResponse {

    private BigDecimal balance;
}
