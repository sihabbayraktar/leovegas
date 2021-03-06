package org.leovegas.wallet.controller;

import lombok.RequiredArgsConstructor;
import org.leovegas.wallet.business.PaymentService;
import org.leovegas.wallet.exception.NonUniqueTransactionException;
import org.leovegas.wallet.exception.WalletNotFoundException;
import org.leovegas.wallet.model.request.UserCreditRequest;
import org.leovegas.wallet.model.request.UserDebitRequest;
import org.leovegas.wallet.model.response.UserCreditResponse;
import org.leovegas.wallet.model.response.UserDebitResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentResource {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PaymentService paymentService;

    @PutMapping(value = "/debit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDebitResponse> debitUser(
            @Valid @RequestBody UserDebitRequest request)  {
        logger.info("PaymentController.debitUser is called with " + request);
        return new ResponseEntity<>(paymentService.debit(request), HttpStatus.OK);
    }

    @PutMapping(value = "/credit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserCreditResponse> creditUser(
            @Valid @RequestBody UserCreditRequest request) throws NonUniqueTransactionException, WalletNotFoundException {
        logger.info("PaymentController.creditUser is called with " + request);
        return new ResponseEntity<>(paymentService.credit(request), HttpStatus.OK);
    }
}
