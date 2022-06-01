package org.leovegas.wallet.business;

import lombok.RequiredArgsConstructor;
import org.leovegas.wallet.model.request.BalanceRequest;
import org.leovegas.wallet.model.response.AllBalanceResponse;
import org.leovegas.wallet.model.response.BalanceResponse;
import org.leovegas.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WalletService walletService;

    public BalanceResponse getUserBalance(BalanceRequest request) {
        logger.info("BalanceService.getUserBalance is called with " + request);
        return new BalanceResponse(walletService.getUserWalletById(request.getUserId()).getBalance());
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
