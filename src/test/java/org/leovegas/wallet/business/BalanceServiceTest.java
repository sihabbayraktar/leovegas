package org.leovegas.wallet.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.entity.Wallet;
import org.leovegas.wallet.exception.AuthorizationException;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.model.request.BalanceRequest;
import org.leovegas.wallet.model.response.AllBalanceResponse;
import org.leovegas.wallet.model.response.BalanceResponse;
import org.leovegas.wallet.service.WalletService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BalanceServiceTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private BalanceService balanceService;

    private Wallet wallet;

    @BeforeEach
    public void setup(){
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(100L));
    }

    @Test
    @WithMockUser(username = "07344d08-ec0d-11ec-8ea0-0242ac120002", password = "userpass", roles = {"USER"})
    public void whenBalanceIsCorrectThenReturnBalanceIsCorrect() {
        when(walletService.getUserWalletByUserId(any())).thenReturn(wallet);
        BalanceRequest request = new BalanceRequest("07344d08-ec0d-11ec-8ea0-0242ac120002");
        BalanceResponse response = balanceService.getUserBalance(request);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(wallet.getBalance(), response.getBalance()));
    }

    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenBalanceIsCorrectAndUserTryToAccessAnotherUserBalanceThenThrowAuthorizationException() {
        when(walletService.getUserWalletByUserId(any())).thenReturn(wallet);
        BalanceRequest request = new BalanceRequest("07344d08-ec0d-11ec-8ea0-0242ac120002");
        assertThrows(AuthorizationException.class, () -> balanceService.getUserBalance(request));
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
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenWalletNotFoundThenThrowsWalletNotFoundException() {
        when(walletService.getUserWalletByUserId(any())).thenThrow(WalletNotFoundException.class);
        BalanceRequest request = new BalanceRequest("5fc03087-d265-11e7-b8c6-83e29cd24f4c");
        assertThrows(WalletNotFoundException.class, () -> balanceService.getUserBalance(request));
    }


}
