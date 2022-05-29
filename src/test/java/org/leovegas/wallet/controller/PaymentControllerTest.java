package org.leovegas.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.leovegas.wallet.exception.BalanceInsufficientException;
import org.leovegas.wallet.exception.NonUniqueTransactionException;
import org.leovegas.wallet.model.request.UserCreditRequest;
import org.leovegas.wallet.model.request.UserDebitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentControllerTest  {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @Order(1)
    public void debitTest() throws Exception {
        UserDebitRequest request = new UserDebitRequest(1L, BigDecimal.valueOf(20), 1L);
        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(80.0));
    }

    @Test
    @Order(2)
    public void creditTest() throws Exception {
        UserCreditRequest request = new UserCreditRequest(2L, BigDecimal.valueOf(20), 2L);
        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(220.0));
    }

    @Test
    @Order(3)
    public void nonUniqueTransactionExceptionForCreditTest() throws Exception {
        UserCreditRequest request = new UserCreditRequest(3L, BigDecimal.valueOf(20), 1L);
        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NonUniqueTransactionException))
                .andExpect(result -> assertEquals("Transaction id: 1 is not unique.", result.getResolvedException().getMessage()));

    }

    @Test
    @Order(4)
    public void nonUniqueTransactionExceptionForDebitTest() throws Exception {
        UserCreditRequest request = new UserCreditRequest(3L, BigDecimal.valueOf(20), 1L);
        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NonUniqueTransactionException))
                .andExpect(result -> assertEquals("Transaction id: 1 is not unique.", result.getResolvedException().getMessage()));

    }

    @Test
    @Order(5)
    public void balanceInsufficientExceptionTest() throws Exception {
        UserDebitRequest request = new UserDebitRequest(3L, BigDecimal.valueOf(500), 10L);
        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BalanceInsufficientException))
                .andExpect(result -> assertEquals("Balance is insufficient to debit", result.getResolvedException().getMessage()));

    }

    @Test
    @Order(6)
    public void validityErrorTest() throws Exception {
        UserDebitRequest request = new UserDebitRequest();

        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("amount cannot be null"))
                .andExpect(jsonPath("$.transactionId").value("transaction id cannot be null"))
                .andExpect(jsonPath("$.userId").value("user id cannot be null"));

        request.setUserId(1L);

        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("amount cannot be null"))
                .andExpect(jsonPath("$.transactionId").value("transaction id cannot be null"));


        request.setAmount(BigDecimal.valueOf(10L));

        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.transactionId").value("transaction id cannot be null"));


        request.setAmount(BigDecimal.valueOf(-100L));
        request.setTransactionId(11L);

        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("amount cannot be less than 0"));


        UserCreditRequest request1 = new UserCreditRequest();

        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("amount cannot be null"))
                .andExpect(jsonPath("$.transactionId").value("transaction id cannot be null"))
                .andExpect(jsonPath("$.userId").value("user id cannot be null"));

        request1.setUserId(1L);

        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("amount cannot be null"))
                .andExpect(jsonPath("$.transactionId").value("transaction id cannot be null"));


        request1.setAmount(BigDecimal.valueOf(10L));

        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.transactionId").value("transaction id cannot be null"));


        request1.setAmount(BigDecimal.valueOf(-100L));
        request1.setTransactionId(12L);

        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("amount cannot be less than 0"));

    }

    /*
    @Test
    @Order(7)
    public void concurencyTest() throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(4);
        for(int i = 0; i < 100; i++) {
            service.submit(() -> {
               mockMvc.perform()
            });
        }
    }

    */




}
