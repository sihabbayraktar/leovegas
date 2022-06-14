package org.leovegas.wallet.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.NonUniqueTransactionException;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.model.request.UserCreditRequest;
import org.leovegas.wallet.model.request.UserDebitRequest;
import org.leovegas.wallet.model.response.UserCreditResponse;
import org.leovegas.wallet.model.response.UserDebitResponse;
import org.leovegas.wallet.service.TransactionService;
import org.leovegas.wallet.service.WalletService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private WalletService walletService;

    private UserDebitRequest debitRequest;
    private UserCreditRequest creditRequest;
    private Wallet wallet;
    private Transaction debitTransaction;
    private Transaction creditTransaction;

    @BeforeEach
    public void setup() {

        UUID userId = UUID.randomUUID();
        debitRequest = new UserDebitRequest(userId.toString(), BigDecimal.valueOf(100L), UUID.randomUUID().toString());
        creditRequest = new UserCreditRequest(userId.toString(), BigDecimal.valueOf(100L), UUID.randomUUID().toString());

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(200L));
        wallet.setUserId(userId);

        debitTransaction = Transaction.builder().transactionType(TransactionType.DEBIT)
                .amount(BigDecimal.valueOf(100L)).transactionId(UUID.randomUUID()).wallet(wallet).build();

        creditTransaction = Transaction.builder().transactionType(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(100L)).transactionId(UUID.randomUUID()).wallet(wallet).build();

    }

    @Test
    public void whenDebitIsCorrectThenReturnBalanceIsCorrect() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenReturn(wallet);
        UserDebitResponse response = paymentService.debit(debitRequest);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(wallet.getBalance(), response.getBalance())
        );
    }


    @Test
    public void whenDebitTransactionIsNotUniqueThenThrowsNonUniqueTransactionException() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenReturn(createWalletForNonUniqueTransaction());
        when(transactionService.getTransactionByTransactionId(any())).thenReturn(debitTransaction);
        assertThrows(NonUniqueTransactionException.class, () -> paymentService.debit(debitRequest));
    }

    @Test
    public void whenDebitUserIsNotFoundThenThrowsWalletNotFoundException() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenThrow(WalletNotFoundException.class);
        assertThrows(WalletNotFoundException.class, () ->paymentService.debit(debitRequest));
    }

    @Test
    public void whenCreditIsCorrectThenReturnBalanceIsCorrect() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenReturn(wallet);
        UserCreditResponse response = paymentService.credit(creditRequest);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(wallet.getBalance(), response.getBalance()));
    }

    @Test
    public void whenCreditTransactionIsNotUniqueThenThrowsNonUniqueTransactionException() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenReturn(createWalletForNonUniqueTransaction());
        when(transactionService.getTransactionByTransactionId(any())).thenReturn(creditTransaction);
        assertThrows(NonUniqueTransactionException.class, () -> paymentService.credit(creditRequest));
    }

    @Test
    public void whenCreditUserIsNotFoundThenThrowsWalletNotFoundException() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenThrow(WalletNotFoundException.class);
        assertThrows(WalletNotFoundException.class, () -> paymentService.credit(creditRequest));
    }

    private Wallet createWalletForNonUniqueTransaction() {
        Wallet walletForNonUniqueTransaction = new Wallet();
        wallet.setId(2L);
        return walletForNonUniqueTransaction;
    }


}
