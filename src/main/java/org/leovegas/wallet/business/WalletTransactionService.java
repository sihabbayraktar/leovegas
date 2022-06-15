package org.leovegas.wallet.business;

import lombok.RequiredArgsConstructor;
import org.leovegas.wallet.model.dto.TransactionHistory;
import org.leovegas.wallet.model.request.UserTransactionHistoryRequest;
import org.leovegas.wallet.model.response.UserTransactionHistoryResponse;
import org.leovegas.wallet.service.UserService;
import org.leovegas.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletTransactionService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WalletService walletService;

    private final UserService userService;

    public UserTransactionHistoryResponse getUserTransactionHistory(UserTransactionHistoryRequest request) {
        logger.info("TransactionService.getUserTransactionHistory is called with " + request);
        userService.checkUserIsAccesingAnotherUserTransactionHistory(request.getUserId());
        UserTransactionHistoryResponse response = new UserTransactionHistoryResponse();
        walletService.getUserWalletByUserId(UUID.fromString(request.getUserId())).getTransactionList().stream().forEach(transaction -> {
           response.addTransactionHistory(new TransactionHistory(transaction));
        });
        return response;
    }
}
