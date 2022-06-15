package org.leovegas.wallet.business;

import lombok.RequiredArgsConstructor;
import org.leovegas.wallet.model.request.BalanceRequest;
import org.leovegas.wallet.model.response.AllBalanceResponse;
import org.leovegas.wallet.model.response.BalanceResponse;
import org.leovegas.wallet.service.UserService;
import org.leovegas.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BalanceService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WalletService walletService;

    private final UserService userService;

    public BalanceResponse getUserBalance(BalanceRequest request) {
        logger.info("BalanceService.getUserBalance is called with " + request);
        userService.checkUserIsAccessingAnotherUserBalance(request.getUserId());
        return new BalanceResponse(walletService.getUserWalletByUserId(UUID.fromString(request.getUserId())).getBalance());
    }

    public AllBalanceResponse getAllBalance() {
        logger.info("BalanceService.getAllBalance is called");
        AllBalanceResponse response = new AllBalanceResponse();
        walletService.getAllWallets().stream().forEach(wallet -> {
          response.addBalance(new BalanceResponse(wallet.getBalance()));
        });

        return response;
    }
}
