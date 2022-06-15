package org.leovegas.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.exception.AuthorizationException;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.model.request.BalanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenWalletIsExistThenReturnExpectedBalanceIsCorrect() throws Exception {
        BalanceRequest request = new BalanceRequest("5fc03087-d265-11e7-b8c6-83e29cd24f4c");
        mockMvc.perform(get("/wallet/userbalance")
        .contentType(APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100.0));
    }

    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenUserTryToAccessBalanceOfAnotherUserThenThrowsAuthorizationException() throws Exception {

        BalanceRequest request = new BalanceRequest("07344d08-ec0d-11ec-8ea0-0242ac120002");
        mockMvc.perform(get("/wallet/userbalance")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthorizationException))
                .andExpect(result -> assertEquals("Not allowed to see user balance", result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "ADMIN", password = "adminpass", roles = {"ADMIN"})
    public void whenWalletIsNotExistThenThrowsWalletNotFoundException() throws Exception {
        String userId = UUID.randomUUID().toString();
        BalanceRequest request = new BalanceRequest(userId);
        mockMvc.perform(get("/wallet/userbalance")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof WalletNotFoundException))
                .andExpect(result -> assertEquals("Wallet not found with User Id " + userId, result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "ADMIN", password = "adminpass", roles = {"ADMIN"})
    public void whenAllBalancesAreExistForCheckingAdminThenReturnExpectedBalanceList() throws Exception{
        mockMvc.perform(get("/wallet/allbalance")
        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.allBalanceList", hasSize(4)))
                .andExpect(jsonPath("$.allBalanceList[0].balance").value(100.0))
                .andExpect(jsonPath("$.allBalanceList[1].balance").value(200.0))
                .andExpect(jsonPath("$.allBalanceList[2].balance").value(300.0))
                .andExpect(jsonPath("$.allBalanceList[3].balance").value(50.0));

    }

    @Test
    @WithMockUser(username = "5fc03087-d265-11e7-b8c6-83e29cd24f4c", password = "userpass", roles = {"USER"})
    public void whenAllBalancesAreExistForCheckingUserThenReturnForbiddenResponse() throws Exception{
        mockMvc.perform(get("/wallet/allbalance")
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "ADMIN", password = "adminpass", roles = {"ADMIN"})
    public void whenBalanceRequestIsNotInWantedFormThenThrowsValidityError() throws Exception {
        BalanceRequest request = new BalanceRequest();
        mockMvc.perform(get("/wallet/userbalance")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("userId"))
                .andExpect(jsonPath("$.errors[0].message").value("user id cannot be null"));
    }


}
