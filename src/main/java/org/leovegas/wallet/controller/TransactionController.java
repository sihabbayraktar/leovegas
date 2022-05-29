package org.leovegas.wallet.controller;

import org.leovegas.wallet.business.WalletTransactionService;
import org.leovegas.wallet.exception.UserNotFoundException;
import org.leovegas.wallet.model.request.UserTransactionHistoryRequest;
import org.leovegas.wallet.model.response.UserTransactionHistoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WalletTransactionService wallettransactionService;

    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTransactionHistoryResponse> getUserTransactionHistory(
            @Valid @RequestBody UserTransactionHistoryRequest request) throws UserNotFoundException {
        logger.info("TransactionController.getUserTransactionHistory is called with " + request);
        return new ResponseEntity<>(wallettransactionService.getUserTransactionHistory(request), HttpStatus.OK);
    }
}
