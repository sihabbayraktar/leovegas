package org.leovegas.wallet.service;

import lombok.RequiredArgsConstructor;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TransactionRepository transactionRepository;

    public Transaction getByTransactionId(UUID transactionId) {
        logger.info("TransactionService.getTransactionById method is called with transactionId: "+ transactionId);
        return transactionRepository.findByTransactionId(transactionId).orElse(null);
    }


    public void saveTransaction(Transaction transaction) {
        logger.info("TransactionService.saveTransaction method is called");
        transactionRepository.save(transaction);
    }


}
