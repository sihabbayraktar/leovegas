package org.leovegas.wallet.model.response;

import lombok.Data;
import org.leovegas.wallet.model.dto.TransactionHistory;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserTransactionHistoryResponse {

    private List<TransactionHistory> transactionHistoryList;

    public UserTransactionHistoryResponse() {
        this.transactionHistoryList = new ArrayList<>();
    }

    public void addTransactionHistory(TransactionHistory transactionHistory) {
        transactionHistoryList.add(transactionHistory);
    }

}
