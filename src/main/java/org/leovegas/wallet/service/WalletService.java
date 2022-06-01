package org.leovegas.wallet.service;

import lombok.RequiredArgsConstructor;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.UserNotFoundException;
import org.leovegas.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WalletRepository walletRepository;

    @Transactional
    public Wallet getUserWalletById(Long userId) {
        logger.info("WalletService.findByUserId method is called with userId: "+ userId);
        return walletRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("User Id " + userId +" is not found"));
    }


    public List<Wallet> getAllWallets() {
        logger.info("WalletService.getAllWallets method is called");
        return walletRepository.findAll();
    }

    @Transactional(rollbackFor = {Exception.class, Error.class})
    public void saveWallet(Wallet wallet) {
        logger.info("WalletService.saveWallet method is called");
        walletRepository.save(wallet);
    }


}
