package org.leovegas.wallet.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.model.request.BalanceRequest;
import org.leovegas.wallet.model.response.AllBalanceResponse;
import org.leovegas.wallet.model.response.BalanceResponse;
import org.leovegas.wallet.service.WalletService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BalanceServiceTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private BalanceService balanceService;

    private BalanceRequest request;
    private Wallet wallet;

    @BeforeEach
    public void setup(){
        request = new BalanceRequest(UUID.randomUUID().toString());
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(100L));
    }

    @Test
    public void whenBalanceIsCorrectThenReturnBalanceIsCorrect() {
        when(walletService.getUserWalletByUserId(any())).thenReturn(wallet);
        BalanceResponse response = balanceService.getUserBalance(request);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(wallet.getBalance(), response.getBalance()));
    }

    @Test
    public void whenAllBalanceIsCorrectThenReturnListOfBalanceIsCorrect() {
        when(walletService.getAllWallets()).thenReturn(List.of(wallet));
        AllBalanceResponse response = balanceService.getAllBalance();
        assertAll(
                () -> assertNotNull(response),
                () -> assertFalse(response.getAllBalanceList().isEmpty()),
                () -> assertEquals(response.getAllBalanceList().size(), 1)
        );
    }

    @Test
    public void whenWalletNotFoundThenThrowsWalletNotFoundException() {
        when(walletService.getUserWalletByUserId(any())).thenThrow(WalletNotFoundException.class);
        assertThrows(WalletNotFoundException.class, () -> balanceService.getUserBalance(request));
    }


}
