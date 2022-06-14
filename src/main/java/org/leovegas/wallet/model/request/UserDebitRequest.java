package org.leovegas.wallet.model.request;


import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDebitRequest {

    @NotNull(message = "user id cannot be null")
    private String userId;

    @Min(value = 0, message = "amount cannot be less than 0")
    @NotNull(message = "amount cannot be null")
    private BigDecimal amount;

    @NotNull(message = "transaction id cannot be null")
    private String transactionId;
}
