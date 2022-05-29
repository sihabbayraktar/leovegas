package org.leovegas.wallet.business;

import org.leovegas.wallet.exception.UserNotFoundException;
import org.leovegas.wallet.model.dto.TransactionHistory;
import org.leovegas.wallet.model.request.UserTransactionHistoryRequest;
import org.leovegas.wallet.model.response.UserTransactionHistoryResponse;
import org.leovegas.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletTransactionService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WalletService walletService;

    public UserTransactionHistoryResponse getUserTransactionHistory(UserTransactionHistoryRequest request) throws UserNotFoundException {
        logger.info("TransactionService.getUserTransactionHistory is called with " + request);
        UserTransactionHistoryResponse response = new UserTransactionHistoryResponse();
        walletService.getUserWalletById(request.getUserId()).getTransactionList().stream().forEach(transaction -> {
           response.addTransactionHistory(new TransactionHistory(transaction));
        });
        return response;
    }
}
