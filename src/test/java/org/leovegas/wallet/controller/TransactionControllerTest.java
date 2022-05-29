package org.leovegas.wallet.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.exception.UserNotFoundException;
import org.leovegas.wallet.model.request.UserTransactionHistoryRequest;
import org.leovegas.wallet.service.TransactionService;
import org.leovegas.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @Transactional
    void insertDummyData() throws Exception {
        transactionService.saveTransaction(Transaction.builder().transactionType(TransactionType.DEBIT)
                .wallet(walletService.getUserWalletById(1L)).amount(BigDecimal.valueOf(100L)).transactionTime(new Date()).id(1L).build());
        transactionService.saveTransaction(Transaction.builder().transactionType(TransactionType.CREDIT)
                .wallet(walletService.getUserWalletById(1L)).amount(BigDecimal.valueOf(100L)).transactionTime(new Date()).id(2L).build());
        transactionService.saveTransaction(Transaction.builder().transactionType(TransactionType.DEBIT)
                .wallet(walletService.getUserWalletById(1L)).amount(BigDecimal.valueOf(100L)).transactionTime(new Date()).id(3L).build());
    }

    @Test
    void transactionHistoryTest() throws Exception {
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest(1L);

        mockMvc.perform(get("/transaction/history")
        .contentType(APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.transactionHistoryList", hasSize(3)))
                .andExpect(jsonPath("$.transactionHistoryList[0].transactionType").value("DEBIT"))
                .andExpect(jsonPath("$.transactionHistoryList[0].amount").value(100.0))
                .andExpect(jsonPath("$.transactionHistoryList[1].transactionType").value("CREDIT"))
                .andExpect(jsonPath("$.transactionHistoryList[1].amount").value(100.0))
                .andExpect(jsonPath("$.transactionHistoryList[2].transactionType").value("DEBIT"))
                .andExpect(jsonPath("$.transactionHistoryList[2].amount").value(100.0));
    }

    @Test
    void emptyTransactionHistoryTest() throws Exception {
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest(2L);

        mockMvc.perform(get("/transaction/history")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.transactionHistoryList", hasSize(0)));
    }

    @Test
    void userNotFoundExceptionTest() throws Exception {
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest(5L);
        mockMvc.perform(get("/transaction/history")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User Id 5 is not found", result.getResolvedException().getMessage()));
    }

    @Test
    void validityErrorTest() throws Exception {
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest();
        mockMvc.perform(get("/transaction/history")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("user id cannot be null"));

    }



}
