package org.leovegas.wallet.model.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserTransactionHistoryRequest {

    @NotNull(message = "user id cannot be null")
    private Long userId;
}