package org.leovegas.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.exception.UserNotFoundException;
import org.leovegas.wallet.model.request.BalanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void userBalanceTest() throws Exception {
        BalanceRequest request = new BalanceRequest(1L);
        mockMvc.perform(get("/wallet/userbalance")
        .contentType(APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100.0));
    }

    @Test
    public void userBalanceThrowsException() throws Exception {
        BalanceRequest request = new BalanceRequest(5L);
        mockMvc.perform(get("/wallet/userbalance")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User Id 5 is not found", result.getResolvedException().getMessage()));
    }

    @Test
    public void allbalanceTest() throws Exception{
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
    public void validityErrorTest() throws Exception {
        BalanceRequest request = new BalanceRequest();
        mockMvc.perform(get("/wallet/userbalance")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("user id cannot be null"));

    }


}
