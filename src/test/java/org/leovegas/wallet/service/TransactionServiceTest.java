package org.leovegas.wallet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class TransactionServiceTest {

   @Mock
   private TransactionRepository transactionRepository;

   @InjectMocks
   private TransactionService transactionService;

   private Transaction transaction;

   @BeforeEach
   public void setup() {

       transaction = new Transaction();
       transaction.setId(1L);
       transaction.setAmount(BigDecimal.valueOf(100L));
       transaction.setTransactionTime(new Date());
       transaction.setTransactionType(TransactionType.CREDIT);

   }

   @Test
   public void transactionByIdTest() {
       when(transactionRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(transaction));
       Transaction transactionById = transactionService.getTransactionById(1L);
       assertAll(
               () -> assertNotNull(transactionById),
               () -> assertEquals(transactionById.getAmount(), transaction.getAmount()),
               () -> assertEquals(transactionById.getTransactionType().name(), transaction.getTransactionType().name()),
               () -> assertEquals(transactionById.getTransactionTime(), transaction.getTransactionTime())
       );
   }

   @Test
   public void transactionSaveTest() {
       when(transactionRepository.save(any())).thenReturn(transaction);
       assertAll(
               () -> assertNotNull(transaction)
       );
   }

}
