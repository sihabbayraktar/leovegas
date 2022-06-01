package org.leovegas.wallet.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.leovegas.wallet.entity.Transaction;
import org.leovegas.wallet.entity.TransactionType;
import org.leovegas.wallet.repository.TransactionRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionServiceTest {

   @Mock
   private TransactionRepository transactionRepository;

   @InjectMocks
   private TransactionService transactionService;

   private Transaction transaction;

   @BeforeAll
   public void setup() {

       transaction = new Transaction();
       transaction.setId(10L);
       transaction.setAmount(BigDecimal.valueOf(100L));
       transaction.setTransactionTime(new Date());
       transaction.setTransactionType(TransactionType.CREDIT);

   }

   @Test
   public void whenTransactionIsCorrectThenReturnTransactionIsCorrect() {
       when(transactionRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(transaction));
       Transaction transactionById = transactionService.getTransactionById(10L);
       assertAll(
               () -> assertNotNull(transactionById),
               () -> assertEquals(transactionById.getAmount(), transaction.getAmount()),
               () -> assertEquals(transactionById.getTransactionType().name(), transaction.getTransactionType().name()),
               () -> assertEquals(transactionById.getTransactionTime(), transaction.getTransactionTime())
       );
   }

   @Test
   public void whenTransactionIsSavedThenReturnNotNull() {
       when(transactionRepository.save(any())).thenReturn(transaction);
       assertAll(
               () -> assertNotNull(transaction)
       );
   }

}
