package org.leovegas.wallet.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.AuthorizationException;
import org.leovegas.wallet.exception.NonUniqueTransactionException;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.model.request.UserCreditRequest;
import org.leovegas.wallet.model.request.UserDebitRequest;
import org.leovegas.wallet.model.response.UserCreditResponse;
import org.leovegas.wallet.model.response.UserDebitResponse;
import org.leovegas.wallet.service.TransactionService;
import org.leovegas.wallet.service.UserService;
import org.leovegas.wallet.service.WalletService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private WalletService walletService;

    @Mock
    private UserService userService;

    private UserDebitRequest debitRequest;
    private UserCreditRequest creditRequest;
    private Wallet wallet;
    private Transaction debitTransaction;
    private Transaction creditTransaction;

    @BeforeEach
    public void setup() {

        UUID userId = UUID.fromString("5fc03087-d265-11e7-b8c6-83e29cd24f4c");
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
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenDebitIsCorrectThenReturnBalanceIsCorrect() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenReturn(wallet);
        doThrow(AuthorizationException.class).when(userService).checkUserIsMakingTransactionForAnotherUser(not(eq(debitRequest.getUserId())));
        UserDebitResponse response = paymentService.debit(debitRequest);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(wallet.getBalance(), response.getBalance())
        );
    }


    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenDebitTransactionIsNotUniqueThenThrowsNonUniqueTransactionException() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenReturn(createWalletForNonUniqueTransaction());
        when(transactionService.getTransactionByTransactionId(any())).thenReturn(debitTransaction);
        doThrow(AuthorizationException.class).when(userService).checkUserIsMakingTransactionForAnotherUser(not(eq(debitRequest.getUserId())));
        assertThrows(NonUniqueTransactionException.class, () -> paymentService.debit(debitRequest));
    }

    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenDebitWalletIsNotFoundThenThrowsWalletNotFoundException() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenThrow(WalletNotFoundException.class);
        doThrow(AuthorizationException.class).when(userService).checkUserIsMakingTransactionForAnotherUser(not(eq(debitRequest.getUserId())));
        assertThrows(WalletNotFoundException.class, () ->paymentService.debit(debitRequest));
    }

    @Test
    @WithMockUser(username = "07344d08-ec0d-11ec-8ea0-0242ac120002", password = "userpass", roles = {"USER"})
    public void whenDebitIsTriedByAnotherUserThenThrowsAuthorizationException() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenReturn(wallet);
        doThrow(AuthorizationException.class).when(userService).checkUserIsMakingTransactionForAnotherUser(not(eq("07344d08-ec0d-11ec-8ea0-0242ac120002")));
        assertThrows(AuthorizationException.class, () -> paymentService.debit(debitRequest));
    }

    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenCreditIsCorrectThenReturnBalanceIsCorrect() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenReturn(wallet);
        doThrow(AuthorizationException.class).when(userService).checkUserIsMakingTransactionForAnotherUser(not(eq(creditRequest.getUserId())));
        UserCreditResponse response = paymentService.credit(creditRequest);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(wallet.getBalance(), response.getBalance()));
    }

    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenCreditTransactionIsNotUniqueThenThrowsNonUniqueTransactionException() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenReturn(createWalletForNonUniqueTransaction());
        when(transactionService.getTransactionByTransactionId(any())).thenReturn(creditTransaction);
        doThrow(AuthorizationException.class).when(userService).checkUserIsMakingTransactionForAnotherUser(not(eq(creditRequest.getUserId())));
        assertThrows(NonUniqueTransactionException.class, () -> paymentService.credit(creditRequest));
    }

    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenCreditWalletIsNotFoundThenThrowsWalletNotFoundException() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenThrow(WalletNotFoundException.class);
        doThrow(AuthorizationException.class).when(userService).checkUserIsMakingTransactionForAnotherUser(not(eq(creditRequest.getUserId())));
        assertThrows(WalletNotFoundException.class, () -> paymentService.credit(creditRequest));
    }

    @Test
    @WithMockUser(username = "07344d08-ec0d-11ec-8ea0-0242ac120002", password = "userpass", roles = {"USER"})
    public void whenCreditIsTriedByAnotherUserThenThrowsAuthorizationException() {
        when(walletService.getUserWalletForUpdateByUserId(any())).thenReturn(wallet);
        doThrow(AuthorizationException.class).when(userService).checkUserIsMakingTransactionForAnotherUser(not(eq("07344d08-ec0d-11ec-8ea0-0242ac120002")));
        assertThrows(AuthorizationException.class, () -> paymentService.credit(creditRequest));
    }

    private Wallet createWalletForNonUniqueTransaction() {
        Wallet walletForNonUniqueTransaction = new Wallet();
        wallet.setId(2L);
        return walletForNonUniqueTransaction;
    }

}
