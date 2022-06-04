package org.leovegas.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.leovegas.wallet.exception.BalanceInsufficientException;
import org.leovegas.wallet.exception.NonUniqueTransactionException;
import org.leovegas.wallet.model.request.UserCreditRequest;
import org.leovegas.wallet.model.request.UserDebitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @Order(value = 1)
    public void whenDebitMoneyThenReturnSuccessAndExpectedBalanceIsCorrect() throws Exception {

        UserDebitRequest request = new UserDebitRequest(1L, BigDecimal.valueOf(20), 500L);
        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(80.0));
    }

    @Test
    @Order(value = 2)
    public void whenCreditMoneyThenReturnSuccessAndExpectedBalanceIsCorrect() throws Exception {

        UserCreditRequest request = new UserCreditRequest(2L, BigDecimal.valueOf(20), 200L);
        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(220.0));
    }

    @Test
    @Order(value = 3)
    public void whenCreditMoneyHasNoUniqueTransactionIdThenThrowsNonUniqueTransactionException() throws Exception {

        UserCreditRequest request = new UserCreditRequest(3L, BigDecimal.valueOf(20), 200L);
        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NonUniqueTransactionException))
                .andExpect(result -> assertEquals("Transaction id: 200 is not unique.", result.getResolvedException().getMessage()));

    }

    @Test
    @Order(value = 4)
    public void whenDebitMoneyHasNoUniqueTransactionIdThenThrowsNonUniqueTransactionException() throws Exception {

        UserCreditRequest request = new UserCreditRequest(3L, BigDecimal.valueOf(20), 500L);
        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NonUniqueTransactionException))
                .andExpect(result -> assertEquals("Transaction id: 500 is not unique.", result.getResolvedException().getMessage()));

    }

    @Test
    @Order(value = 5)
    public void whenDebitMoneyBalanceIsNotSuffucientThenThrowsBalanceInsufficientException() throws Exception {

        UserDebitRequest request = new UserDebitRequest(3L, BigDecimal.valueOf(500), 15L);
        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BalanceInsufficientException))
                .andExpect(result -> assertEquals("Balance is insufficient to debit", result.getResolvedException().getMessage()));

    }

    @Test
    @Order(value = 6)
    public void whenCreditOrDebitRequestIsNotInWantedFormThenGiveValidityError() throws Exception {

        UserDebitRequest request = new UserDebitRequest();
        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("amount cannot be null"))
                .andExpect(jsonPath("$.errors[1].field").value("transactionId"))
                .andExpect(jsonPath("$.errors[1].message").value("transaction id cannot be null"))
                .andExpect(jsonPath("$.errors[2].field").value("userId"))
                .andExpect(jsonPath("$.errors[2].message").value("user id cannot be null"));



        request.setUserId(1L);

        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("amount cannot be null"))
                .andExpect(jsonPath("$.errors[1].field").value("transactionId"))
                .andExpect(jsonPath("$.errors[1].message").value("transaction id cannot be null"));


        request.setAmount(BigDecimal.valueOf(10L));

        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("transactionId"))
                .andExpect(jsonPath("$.errors[0].message").value("transaction id cannot be null"));


        request.setAmount(BigDecimal.valueOf(-100L));
        request.setTransactionId(11L);

        mockMvc.perform(post("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("amount cannot be less than 0"));


        UserCreditRequest request1 = new UserCreditRequest();

        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("amount cannot be null"))
                .andExpect(jsonPath("$.errors[1].field").value("transactionId"))
                .andExpect(jsonPath("$.errors[1].message").value("transaction id cannot be null"))
                .andExpect(jsonPath("$.errors[2].field").value("userId"))
                .andExpect(jsonPath("$.errors[2].message").value("user id cannot be null"));


        request1.setUserId(1L);

        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("amount cannot be null"))
                .andExpect(jsonPath("$.errors[1].field").value("transactionId"))
                .andExpect(jsonPath("$.errors[1].message").value("transaction id cannot be null"));


        request1.setAmount(BigDecimal.valueOf(10L));

        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("transactionId"))
                .andExpect(jsonPath("$.errors[0].message").value("transaction id cannot be null"));


        request1.setAmount(BigDecimal.valueOf(-100L));
        request1.setTransactionId(12L);

        mockMvc.perform(post("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("amount cannot be less than 0"));
    }


    @Test
    @Order(value = 7)
    public void whenCreditAndDebitOccuredSameTimeThenExpectObjectOptimisticLockingFailureException() throws Exception {

        ExecutorService service = Executors.newFixedThreadPool(4);
        for(int i = 0; i < 10; i++) {
            service.submit(() -> {

                try {
                    mockMvc.perform(post("/payment/credit")
                    .contentType(APPLICATION_JSON_VALUE)
                            .accept(APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(
                                    UserCreditRequest.builder().amount(BigDecimal.valueOf(100L)).userId(1L).transactionId(generateTransactionId()).build())));

                    mockMvc.perform(post("/payment/debit")
                            .contentType(APPLICATION_JSON_VALUE)
                            .accept(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(
                                    UserDebitRequest.builder().amount(BigDecimal.valueOf(100L)).userId(1L).transactionId(generateTransactionId()).build())));
                }
                catch (Exception exception) {
                    assertTrue(() -> exception instanceof ObjectOptimisticLockingFailureException);
                }

            });
        }

        service.shutdown();
        if(!service.awaitTermination(60, TimeUnit.SECONDS)) {
            service.shutdown();
        }
    }

    private Long generateTransactionId() {
        return ThreadLocalRandom.current().nextLong(0, Integer.MAX_VALUE);
    }






}
