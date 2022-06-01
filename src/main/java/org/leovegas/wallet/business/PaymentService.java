package org.leovegas.wallet.business;

import lombok.RequiredArgsConstructor;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.BalanceInsufficientException;
import org.leovegas.wallet.exception.NonUniqueTransactionException;
import org.leovegas.wallet.exception.UserNotFoundException;
import org.leovegas.wallet.model.request.UserCreditRequest;
import org.leovegas.wallet.model.request.UserDebitRequest;
import org.leovegas.wallet.model.response.UserCreditResponse;
import org.leovegas.wallet.model.response.UserDebitResponse;
import org.leovegas.wallet.service.TransactionService;
import org.leovegas.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TransactionService transactionService;
    private final WalletService walletService;


    public UserDebitResponse debit(UserDebitRequest request)  {

        logger.info("PaymentService.debit is called with " + request);
        isTransactionUnique(request.getTransactionId());

        Wallet wallet = walletService.getUserWalletById(request.getUserId());

        BigDecimal updatedBalance = wallet.getBalance().subtract(request.getAmount());
        if (updatedBalance.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Balance is insufficient");
            throw new BalanceInsufficientException("Balance is insufficient to debit");
        }

        wallet.setBalance(updatedBalance);
        Transaction transaction = Transaction.builder().id(request.getTransactionId()).transactionType(TransactionType.DEBIT)
                .amount(request.getAmount()).wallet(wallet).build();

        saveWalletAndTransaction(wallet, transaction);

        return new UserDebitResponse(updatedBalance);
    }

    public UserCreditResponse credit(UserCreditRequest request) throws NonUniqueTransactionException, UserNotFoundException {

        logger.info("PaymentService.credit is called with " + request);
        isTransactionUnique(request.getTransactionId());

        Wallet wallet = walletService.getUserWalletById(request.getUserId());

        BigDecimal updatedBalance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(updatedBalance);
        Transaction transaction = Transaction.builder().id(request.getTransactionId()).transactionType(TransactionType.CREDIT)
                .amount(request.getAmount()).wallet(wallet).build();

        saveWalletAndTransaction(wallet, transaction);

        return new UserCreditResponse(updatedBalance);
    }

    private void isTransactionUnique(Long transactionId) throws NonUniqueTransactionException {
        if (transactionService.getTransactionById(transactionId) != null) {
            logger.error("Transaction is not unique");
            throw new NonUniqueTransactionException("Transaction id: " + transactionId + " is not unique.");
        }
    }

    private void saveWalletAndTransaction(Wallet wallet, Transaction transaction) {

        walletService.saveWallet(wallet);
        logger.info("wallet is saved");

        transactionService.saveTransaction(transaction);
        logger.info("transaction is saved");
    }


}
