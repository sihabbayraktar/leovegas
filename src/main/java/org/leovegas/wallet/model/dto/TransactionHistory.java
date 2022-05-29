package org.leovegas.wallet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.leovegas.wallet.entity.Transaction;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class TransactionHistory {

    private String transactionType;
    private BigDecimal amount;
    private Date transactionTime;

    public TransactionHistory(Transaction transaction) {
        this.transactionType = transaction.getTransactionType().name();
        this.amount = transaction.getAmount();
        this.transactionTime = transaction.getTransactionTime();
    }
}
