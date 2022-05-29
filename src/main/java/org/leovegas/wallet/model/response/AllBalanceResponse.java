package org.leovegas.wallet.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AllBalanceResponse {

    private List<BalanceResponse> allBalanceList;

    public AllBalanceResponse() {
        this.allBalanceList = new ArrayList<>();
    }

    public void addBalance(BalanceResponse balanceResponse) {
        allBalanceList.add(balanceResponse);
    }
}
