package org.leovegas.wallet.service;

import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction getTransactionById(Long transactionId) {
        logger.info("Transaction.getTransactionById method is called with transactionId: "+ transactionId);
        return transactionRepository.findById(transactionId).orElse(null);
    }

    @Transactional(rollbackFor = {Exception.class, Error.class})
    public void saveTransaction(Transaction transaction) {
        logger.info("Transaction.saveTransaction method is called");
        transactionRepository.save(transaction);
    }
}
