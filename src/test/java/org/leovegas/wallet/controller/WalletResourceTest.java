package org.leovegas.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.exception.UserNotFoundException;
import org.leovegas.wallet.model.request.BalanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
    public void whenUserWalletIsExistThenReturnExpectedBalanceIsCorrect() throws Exception {
        BalanceRequest request = new BalanceRequest("5fc03087-d265-11e7-b8c6-83e29cd24f4c");
        mockMvc.perform(get("/wallet/userbalance")
        .contentType(APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100.0));
    }

    @Test
    public void whenUserIsNotExistThenThrowsUserNotFoundException() throws Exception {
        String userId = UUID.randomUUID().toString();
        BalanceRequest request = new BalanceRequest(userId);
        mockMvc.perform(get("/wallet/userbalance")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User Id "+ userId +" is not found", result.getResolvedException().getMessage()));
    }

    @Test
    public void whenAllBalancesAreExistThenReturnExpectedBalanceList() throws Exception{
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
