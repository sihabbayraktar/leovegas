package org.leovegas.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.exception.BalanceInsufficientException;
import org.leovegas.wallet.exception.NonUniqueTransactionException;
import org.leovegas.wallet.model.request.UserCreditRequest;
import org.leovegas.wallet.model.request.UserDebitRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    @BeforeAll
    @Transactional
    public void insertDummyData() {

        transactionService.saveTransaction(Transaction.builder().transactionType(TransactionType.CREDIT)
                .wallet(walletService.getUserWalletById(UUID.fromString("1490427c-ec0d-11ec-8ea0-0242ac120002")))
                .amount(BigDecimal.valueOf(100L)).transactionTime(new Date())
                .transactionId(UUID.fromString("176f9994-ec20-11ec-8ea0-0242ac120002")).build());


        transactionService.saveTransaction(Transaction.builder().transactionType(TransactionType.DEBIT)
                .wallet(walletService.getUserWalletById(UUID.fromString("1490427c-ec0d-11ec-8ea0-0242ac120002")))
                .amount(BigDecimal.valueOf(100L)).transactionTime(new Date())
                .transactionId(UUID.fromString("2d1ef8d4-ec20-11ec-8ea0-0242ac120002")).build());

    }


    @Test
    public void whenDebitMoneyThenReturnSuccessAndExpectedBalanceIsCorrect() throws Exception {

        UserDebitRequest request = new UserDebitRequest("5fc03087-d265-11e7-b8c6-83e29cd24f4c",
                BigDecimal.valueOf(20), UUID.randomUUID().toString());
        mockMvc.perform(put("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(80.0));
    }

    @Test
    public void whenCreditMoneyThenReturnSuccessAndExpectedBalanceIsCorrect() throws Exception {

        UserCreditRequest request = new UserCreditRequest("07344d08-ec0d-11ec-8ea0-0242ac120002",
                BigDecimal.valueOf(20), UUID.randomUUID().toString());
        mockMvc.perform(put("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(220.0));
    }

    @Test
    public void whenCreditMoneyHasNoUniqueTransactionIdThenThrowsNonUniqueTransactionException() throws Exception {

        UserCreditRequest request = new UserCreditRequest("07344d08-ec0d-11ec-8ea0-0242ac120002",
                BigDecimal.valueOf(20), "176f9994-ec20-11ec-8ea0-0242ac120002");
        mockMvc.perform(put("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NonUniqueTransactionException))
                .andExpect(result -> assertEquals("Transaction id: 176f9994-ec20-11ec-8ea0-0242ac120002 is not unique.",
                        result.getResolvedException().getMessage()));

    }

    @Test
    public void whenDebitMoneyHasNoUniqueTransactionIdThenThrowsNonUniqueTransactionException() throws Exception {

        UserCreditRequest request = new UserCreditRequest("07344d08-ec0d-11ec-8ea0-0242ac120002",
                BigDecimal.valueOf(20), "176f9994-ec20-11ec-8ea0-0242ac120002");
        mockMvc.perform(put("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NonUniqueTransactionException))
                .andExpect(result -> assertEquals("Transaction id: 176f9994-ec20-11ec-8ea0-0242ac120002 is not unique.",
                        result.getResolvedException().getMessage()));

    }

    @Test
    public void whenDebitMoneyBalanceIsNotSuffucientThenThrowsBalanceInsufficientException() throws Exception {

        UserDebitRequest request = new UserDebitRequest("07344d08-ec0d-11ec-8ea0-0242ac120002",
                BigDecimal.valueOf(500), UUID.randomUUID().toString());
        mockMvc.perform(put("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BalanceInsufficientException))
                .andExpect(result -> assertEquals("Balance is insufficient to debit", result.getResolvedException().getMessage()));

    }

    @Test
    public void whenCreditOrDebitRequestIsNotInWantedFormThenGiveValidityError() throws Exception {

        UserDebitRequest request = new UserDebitRequest();
        mockMvc.perform(put("/payment/debit")
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



        request.setUserId("0ff4ca58-ec0d-11ec-8ea0-0242ac120002");

        mockMvc.perform(put("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("amount cannot be null"))
                .andExpect(jsonPath("$.errors[1].field").value("transactionId"))
                .andExpect(jsonPath("$.errors[1].message").value("transaction id cannot be null"));


        request.setAmount(BigDecimal.valueOf(10L));

        mockMvc.perform(put("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("transactionId"))
                .andExpect(jsonPath("$.errors[0].message").value("transaction id cannot be null"));


        request.setAmount(BigDecimal.valueOf(-100L));
        request.setTransactionId(UUID.randomUUID().toString());

        mockMvc.perform(put("/payment/debit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("amount cannot be less than 0"));


        UserCreditRequest request1 = new UserCreditRequest();

        mockMvc.perform(put("/payment/credit")
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


        request1.setUserId("0ff4ca58-ec0d-11ec-8ea0-0242ac120002");

        mockMvc.perform(put("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("amount cannot be null"))
                .andExpect(jsonPath("$.errors[1].field").value("transactionId"))
                .andExpect(jsonPath("$.errors[1].message").value("transaction id cannot be null"));


        request1.setAmount(BigDecimal.valueOf(10L));

        mockMvc.perform(put("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("transactionId"))
                .andExpect(jsonPath("$.errors[0].message").value("transaction id cannot be null"));


        request1.setAmount(BigDecimal.valueOf(-100L));
        request1.setTransactionId(UUID.randomUUID().toString());

        mockMvc.perform(put("/payment/credit")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("amount cannot be less than 0"));
    }

    /*
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
    */






}
