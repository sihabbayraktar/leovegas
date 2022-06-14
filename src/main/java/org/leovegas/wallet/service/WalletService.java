package org.leovegas.wallet.service;

import lombok.RequiredArgsConstructor;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WalletRepository walletRepository;


    public Wallet getUserWalletByUserId(UUID userId) {
        logger.info("WalletService.getUserWalletByUserId method is called with userId: "+ userId);
        return walletRepository.findWalletByUserId(userId).orElseThrow(() -> new WalletNotFoundException("Wallet not found with User Id " + userId));
    }

    public List<Wallet> getAllWallets() {
        logger.info("WalletService.getAllWallets method is called");
        return walletRepository.findAll();
    }


    public Wallet getUserWalletForUpdateByUserId(UUID userId) {
        logger.info("WalletService.getUserWalletForUpdateByUserId method is called with userId: "+ userId);
        return walletRepository.findWalletForUpdateByUserId(userId).orElseThrow(() -> new WalletNotFoundException("Wallet not found with User Id " + userId));
    }

    public void saveWallet(Wallet wallet) {
        logger.info("WalletService.saveWallet method is called");
        walletRepository.save(wallet);
    }

}
