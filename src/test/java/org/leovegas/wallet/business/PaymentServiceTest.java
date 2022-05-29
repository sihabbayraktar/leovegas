package org.leovegas.wallet.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.NonUniqueTransactionException;
import org.leovegas.wallet.exception.UserNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private UserDebitRequest balanceInsufficientDebitRequest;

    @BeforeEach
    void setup() {
        debitRequest = new UserDebitRequest(1L, BigDecimal.valueOf(100L), 2L);
        creditRequest = new UserCreditRequest(1L, BigDecimal.valueOf(100L), 3L);
        balanceInsufficientDebitRequest = new UserDebitRequest(1L, BigDecimal.valueOf(500L), 5L);

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(200L));
        wallet.setUserId(1L);

        debitTransaction = Transaction.builder().transactionType(TransactionType.DEBIT)
                .amount(BigDecimal.valueOf(100L)).id(1L).wallet(wallet).build();

        creditTransaction = Transaction.builder().transactionType(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(100L)).id(2L).wallet(wallet).build();



        //wallet.setTransactionList(List.of(debitTransaction, creditTransaction));
    }

    @Test
    public void debitTest() throws Exception{
        when(walletService.getUserWalletById(anyLong())).thenReturn(wallet);
        UserDebitResponse response = paymentService.debit(debitRequest);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(wallet.getBalance(), response.getBalance())
        );
    }


    @Test
    public void debitNonUniqueTransactionExceptionTest() {
        when(transactionService.getTransactionById(anyLong())).thenReturn(debitTransaction);
        assertThrows(NonUniqueTransactionException.class, () -> paymentService.debit(debitRequest));

    }

    @Test
    public void debitUserNotFoundExceptionTest() throws Exception {
        when(walletService.getUserWalletById(anyLong())).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () ->paymentService.debit(debitRequest));
    }

    @Test
    public void creditTest() throws Exception {
        when(walletService.getUserWalletById(anyLong())).thenReturn(wallet);
        UserCreditResponse response = paymentService.credit(creditRequest);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(wallet.getBalance(), response.getBalance()));
    }

    @Test
    public void creditNonUniqueTransactionExceptionTest() {
        when(transactionService.getTransactionById(anyLong())).thenReturn(creditTransaction);
        assertThrows(NonUniqueTransactionException.class, () -> paymentService.credit(creditRequest));
    }

    @Test
    public void creditUserNotFoundExceptionTest() throws Exception {
        when(walletService.getUserWalletById(anyLong())).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> paymentService.credit(creditRequest));
    }


}
