package org.leovegas.wallet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.UserNotFoundException;
import org.leovegas.wallet.repository.WalletRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;

    @BeforeEach
    public void setup() {
        wallet = new Wallet();
        wallet.setId(10L);
        wallet.setUserId(10L);
        wallet.setBalance(BigDecimal.valueOf(100L));
        wallet.setVersion(1L);
        wallet.setTransactionList(Collections.emptyList());
    }

    @Test
    public void allWalletsTest() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));
        List<Wallet> allWallets = walletService.getAllWallets();
        assertAll(() ->  assertFalse(allWallets.isEmpty()));
    }

    @Test
    public void userWalletsByIdTest() throws Exception {
        when(walletRepository.findByUserId(anyLong())).thenReturn(Optional.ofNullable(wallet));
        Wallet userWalletById = walletService.getUserWalletById(1L);
        assertAll(() -> assertNotNull(userWalletById),
        () -> assertEquals(wallet.getBalance(), userWalletById.getBalance()));
    }

    @Test
    public void userWalletThrowUserNotFoundExceptionTest() {
        when(walletRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, ()->walletService.getUserWalletById(1L));
    }

    @Test
    public void saveWalletTest() {
        Transaction transaction = Transaction.builder().amount(BigDecimal.valueOf(100L))
                .transactionType(TransactionType.CREDIT)
                .id(1L)
                .transactionTime(new Date()).build();
        wallet.setTransactionList(List.of(transaction));

        when(walletRepository.save(any())).thenReturn(wallet);
        assertAll(
                () -> assertFalse(wallet.getTransactionList().isEmpty())
        );
    }



}
