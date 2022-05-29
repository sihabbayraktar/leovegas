package org.leovegas.wallet.business;

import org.leovegas.wallet.exception.UserNotFoundException;
import org.leovegas.wallet.model.request.BalanceRequest;
import org.leovegas.wallet.model.response.AllBalanceResponse;
import org.leovegas.wallet.model.response.BalanceResponse;
import org.leovegas.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BalanceService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WalletService walletService;

    public BalanceResponse getUserBalance(BalanceRequest request) throws UserNotFoundException {
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
