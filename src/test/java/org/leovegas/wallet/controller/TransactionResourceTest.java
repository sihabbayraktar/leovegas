package org.leovegas.wallet.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.model.request.UserTransactionHistoryRequest;
import org.leovegas.wallet.service.TransactionService;
import org.leovegas.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId = UUID.fromString("1490427c-ec0d-11ec-8ea0-0242ac120002");

    @BeforeAll
    @Transactional
    public void insertDummyData() {
        transactionService.saveTransaction(Transaction.builder().transactionType(TransactionType.DEBIT)
                .wallet(walletService.getUserWalletByUserId(userId))
                .amount(BigDecimal.valueOf(100L)).transactionTime(new Date()).transactionId(UUID.randomUUID()).build());
        transactionService.saveTransaction(Transaction.builder().transactionType(TransactionType.CREDIT)
                .wallet(walletService.getUserWalletByUserId(userId))
                .amount(BigDecimal.valueOf(100L)).transactionTime(new Date()).transactionId(UUID.randomUUID()).build());
        transactionService.saveTransaction(Transaction.builder().transactionType(TransactionType.DEBIT)
                .wallet(walletService.getUserWalletByUserId(userId))
                .amount(BigDecimal.valueOf(100L)).transactionTime(new Date()).transactionId(UUID.randomUUID()).build());
    }

    @Test
    public void whenTransactionHistoryOfUserExistThenReturnSuccessAndExpectedResultIsCorrect() throws Exception {
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest(userId.toString());

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
    public void whenUserExistButTransactionOfThatUserIsNotExistReturnEmptyTransactionList() throws Exception {
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest("0ff4ca58-ec0d-11ec-8ea0-0242ac120002");

        mockMvc.perform(get("/transaction/history")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.transactionHistoryList", hasSize(0)));
    }

    @Test
    public void whenUserIsNotExistThenThrowsWalletNotFoundException() throws Exception {
        String userIdNotExist = UUID.randomUUID().toString();
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest(userIdNotExist);
        mockMvc.perform(get("/transaction/history")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof WalletNotFoundException))
                .andExpect(result -> assertEquals("Wallet not found with User Id " + userIdNotExist,
                        result.getResolvedException().getMessage()));
    }

    @Test
    public void whenUserTransactionHistoryRequestIsNotInWantedFormThenThrowsValidityError() throws Exception {
        UserTransactionHistoryRequest request = new UserTransactionHistoryRequest();
        mockMvc.perform(get("/transaction/history")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("userId"))
                .andExpect(jsonPath("$.errors[0].message").value("user id cannot be null"));

    }
}
