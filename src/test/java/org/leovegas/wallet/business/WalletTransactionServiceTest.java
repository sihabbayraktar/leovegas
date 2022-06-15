package org.leovegas.wallet.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.AuthorizationException;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.model.dto.TransactionHistory;
import org.leovegas.wallet.model.request.UserTransactionHistoryRequest;
import org.leovegas.wallet.model.response.UserTransactionHistoryResponse;
import org.leovegas.wallet.service.UserService;
import org.leovegas.wallet.service.WalletService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WalletTransactionServiceTest {

    @InjectMocks
    private WalletTransactionService walletTransactionService;

    @Mock
    private WalletService walletService;

    @Mock
    private UserService userService;

    private Wallet wallet;
    private TransactionHistory transactionHistory;
    private Transaction transaction;
    private String userId;

    @BeforeEach
    public void setup() {
        userId = "5fc03087-d265-11e7-b8c6-83e29cd24f4c";
        transactionHistory = new TransactionHistory(TransactionType.CREDIT.name(), BigDecimal.valueOf(100L), new Date(1385355600000L));
        wallet = new Wallet();

        transaction = Transaction.builder().transactionId(UUID.randomUUID()).transactionTime(new Date(1385355600000L))
                .transactionType(TransactionType.CREDIT).amount(BigDecimal.valueOf(100L)).wallet(wallet).build();

        wallet.setTransactionList(List.of(transaction));

    }

    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenUserTransactionIsExistThenReturnCorrectTransaction() {
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest(userId);
        when(walletService.getUserWalletByUserId(any())).thenReturn(wallet);
        doThrow(AuthorizationException.class).when(userService).checkUserIsAccesingAnotherUserTransactionHistory(not(eq(userId)));
        UserTransactionHistoryResponse response = walletTransactionService.getUserTransactionHistory(request);
        assertAll(
                () -> assertNotNull(response),
                () -> assertFalse(wallet.getTransactionList().isEmpty()),
                () -> assertEquals(wallet.getTransactionList().size(), 1),
                () -> assertEquals(wallet.getTransactionList().get(0).getTransactionType().name(), transactionHistory.getTransactionType()),
                () -> assertEquals(wallet.getTransactionList().get(0).getAmount(), transactionHistory.getAmount()),
                () -> assertEquals(wallet.getTransactionList().get(0).getTransactionTime().compareTo( transactionHistory.getTransactionTime()), 0)
        );
    }

    @Test
    @WithMockUser(username = "07344d08-ec0d-11ec-8ea0-0242ac120002", password = "userpass", roles = {"USER"})
    public void whenUserTransactionIsExistAndUserTryToAccessAnotherUserTransactionThenThrowsAuthorizationException() {
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest(userId);
        when(walletService.getUserWalletByUserId(any())).thenReturn(wallet);
        doThrow(AuthorizationException.class).when(userService).checkUserIsAccesingAnotherUserTransactionHistory(not(eq("07344d08-ec0d-11ec-8ea0-0242ac120002")));
        assertThrows(AuthorizationException.class, () -> walletTransactionService.getUserTransactionHistory(request));
    }

    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenWalletNotFoundThenThrowsWalletNotFoundException() {
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest(userId);
        when(walletService.getUserWalletByUserId(any())).thenThrow(WalletNotFoundException.class);
        doThrow(AuthorizationException.class).when(userService).checkUserIsAccesingAnotherUserTransactionHistory(not(eq(userId)));
        assertThrows(WalletNotFoundException.class, () -> walletTransactionService.getUserTransactionHistory(request));
    }


}
