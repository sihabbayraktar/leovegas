package org.leovegas.wallet.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.model.dto.TransactionHistory;
import org.leovegas.wallet.model.request.UserTransactionHistoryRequest;
import org.leovegas.wallet.model.response.UserTransactionHistoryResponse;
import org.leovegas.wallet.service.WalletService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WalletTransactionServiceTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletTransactionService walletTransactionService;

    private UserTransactionHistoryRequest request;
    private Wallet wallet;
    private TransactionHistory transactionHistory;
    private Transaction transaction;

    @BeforeEach
    public void setup() {
        request = new UserTransactionHistoryRequest(UUID.randomUUID().toString());
        transactionHistory = new TransactionHistory(TransactionType.CREDIT.name(), BigDecimal.valueOf(100L), new Date(1385355600000L));
        wallet = new Wallet();

        transaction = Transaction.builder().transactionId(UUID.randomUUID()).transactionTime(new Date(1385355600000L))
                .transactionType(TransactionType.CREDIT).amount(BigDecimal.valueOf(100L)).wallet(wallet).build();

        wallet.setTransactionList(List.of(transaction));

    }

    @Test
    public void whenUserTransactionIsExistThenReturnCorrectTransaction() {
        when(walletService.getUserWalletByUserId(any())).thenReturn(wallet);
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
    public void whenUserNotFoundThenThrowsWalletNotFoundException() {
        when(walletService.getUserWalletByUserId(any())).thenThrow(WalletNotFoundException.class);
        assertThrows(WalletNotFoundException.class, () -> walletTransactionService.getUserTransactionHistory(request));
    }


}
