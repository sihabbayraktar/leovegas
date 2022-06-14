package org.leovegas.wallet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.repository.WalletRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;
    private UUID userId;

    @BeforeEach
    public void setup() {

        userId = UUID.randomUUID();
        wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.valueOf(100L));
        wallet.setVersion(1L);
        wallet.setTransactionList(Collections.emptyList());
    }

    @Test
    public void whenAllWalletsAreCorrectThenReturnAllWallets() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));
        List<Wallet> allWallets = walletService.getAllWallets();
        assertAll(() ->  assertFalse(allWallets.isEmpty()));
    }

    @Test
    public void whenWalletIsExistThenReturnBalanceIsCorrect() {
        when(walletRepository.findWalletByUserId(any())).thenReturn(Optional.ofNullable(wallet));
        Wallet userWalletById = walletService.getUserWalletByUserId(userId);
        assertAll(() -> assertNotNull(userWalletById),
        () -> assertEquals(wallet.getBalance(), userWalletById.getBalance()));
    }

    @Test
    public void whenWalletIsNotFoundThenThrowsWalletNotFoundException() {
        when(walletRepository.findWalletByUserId(any())).thenReturn(Optional.empty());
        assertThrows(WalletNotFoundException.class, ()-> walletService.getUserWalletByUserId(userId));
    }

    @Test
    public void whenWalletIsExistWithLockThenReturnBalanceIsCorrect() {
        when(walletRepository.findWalletForUpdateByUserId(any())).thenReturn(Optional.ofNullable(wallet));
        Wallet userWalletById = walletService.getUserWalletForUpdateByUserId(userId);
        assertAll(() -> assertNotNull(userWalletById),
                () -> assertEquals(wallet.getBalance(), userWalletById.getBalance()));
    }

    @Test
    public void whenWalletIsSavedThenReturnCorrectWallet() {
        Transaction transaction = Transaction.builder().amount(BigDecimal.valueOf(100L))
                .transactionType(TransactionType.CREDIT)
                .transactionId(UUID.fromString("9e24f776-ec0d-11ec-8ea0-0242ac120002"))
                .transactionTime(new Date()).build();
        wallet.setTransactionList(List.of(transaction));

        when(walletRepository.save(any())).thenReturn(wallet);
        assertAll(
                () -> assertFalse(wallet.getTransactionList().isEmpty())
        );
    }



}
