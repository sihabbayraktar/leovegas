package org.leovegas.wallet.business;

import lombok.RequiredArgsConstructor;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.BalanceInsufficientException;
import org.leovegas.wallet.exception.NonUniqueTransactionException;
import org.leovegas.wallet.model.request.UserCreditRequest;
import org.leovegas.wallet.model.request.UserDebitRequest;
import org.leovegas.wallet.model.response.TransactionResponse;
import org.leovegas.wallet.model.response.UserCreditResponse;
import org.leovegas.wallet.model.response.UserDebitResponse;
import org.leovegas.wallet.service.TransactionService;
import org.leovegas.wallet.service.UserService;
import org.leovegas.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TransactionService transactionService;
    private final WalletService walletService;
    private final UserService userService;


    @Transactional
    public UserDebitResponse debit(UserDebitRequest request)  {

        logger.info("PaymentService.debit is called with " + request);
        return (UserDebitResponse) modifyBalance(TransactionType.DEBIT, UUID.fromString(request.getUserId()),
                UUID.fromString(request.getTransactionId()), request.getAmount());
    }

    @Transactional
    public UserCreditResponse credit(UserCreditRequest request)  {

        logger.info("PaymentService.credit is called with " + request);
        return (UserCreditResponse) modifyBalance(TransactionType.CREDIT, UUID.fromString(request.getUserId()),
                UUID.fromString(request.getTransactionId()), request.getAmount());
    }


    private TransactionResponse modifyBalance(TransactionType transactionType,
                                              UUID userId, UUID transactionId, BigDecimal amount)  {

        userService.checkUserIsMakingTransactionForAnotherUser(userId.toString());
        Wallet wallet = walletService.getUserWalletForUpdateByUserId(userId);

        Transaction isExistTransaction = transactionService.getTransactionByTransactionId(transactionId);

        // Idempotency and transaction uniqueness check.
        if (isExistTransaction != null) {
            if (isExistTransaction.getWallet().getId().equals(wallet.getId())
                    && isExistTransaction.getAmount().equals(amount)){
                if (transactionType.equals(TransactionType.CREDIT)) {
                    return new UserCreditResponse(wallet.getBalance().add(amount));
                }
                else {
                    return new UserDebitResponse(wallet.getBalance().subtract(amount));
                }
            }
            else {
                logger.error("Transaction is not unique");
                throw new NonUniqueTransactionException("Transaction id: " + transactionId + " is not unique.");
            }
        }

        Transaction transaction2Save = Transaction.builder().transactionId(transactionId).amount(amount)
                .transactionTime(new Date()).build();
        BigDecimal updatedBalance;

        if(transactionType.equals(TransactionType.CREDIT)) {
            logger.info("Transaction is credit and processing");
            transaction2Save.setTransactionType(TransactionType.CREDIT);
            updatedBalance = wallet.getBalance().add(amount);
            wallet.setBalance(updatedBalance);
            transaction2Save.setWallet(wallet);
            saveWalletAndTransaction(wallet, transaction2Save);
            return new UserCreditResponse(updatedBalance);
        }
        else {
            logger.info("Transaction is debit and processing");
            transaction2Save.setTransactionType(TransactionType.DEBIT);
            updatedBalance = wallet.getBalance().subtract(amount);
            if (updatedBalance.compareTo(BigDecimal.ZERO) < 0) {
                logger.error("Balance is insufficient");
                throw new BalanceInsufficientException("Balance is insufficient to debit");
            }
            wallet.setBalance(updatedBalance);
            transaction2Save.setWallet(wallet);
            saveWalletAndTransaction(wallet, transaction2Save);
            return new UserDebitResponse(updatedBalance);
        }
    }


    private void saveWalletAndTransaction(Wallet wallet, Transaction transaction) {

        walletService.saveWallet(wallet);
        logger.info("wallet is saved");

        transactionService.saveTransaction(transaction);
        logger.info("transaction is saved");
    }
}
