package org.leovegas.wallet.controller;

import org.leovegas.wallet.business.BalanceService;
import org.leovegas.wallet.exception.UserNotFoundException;
import org.leovegas.wallet.model.request.BalanceRequest;
import org.leovegas.wallet.model.response.AllBalanceResponse;
import org.leovegas.wallet.model.response.BalanceResponse;
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
@RequestMapping("/wallet")
public class WalletController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BalanceService balanceService;

    @GetMapping(value = "/userbalance", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BalanceResponse> getUserBalance(
            @Valid @RequestBody BalanceRequest request) throws UserNotFoundException {
        logger.info("WalletController.getUserBalance is called with " + request);
        return new ResponseEntity<>(balanceService.getUserBalance(request), HttpStatus.OK);
    }

    @GetMapping(value = "/allbalance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AllBalanceResponse> getAllBalance() {
        logger.info("WalletController.getAllBalance is called");
        return new ResponseEntity<>(balanceService.getAllBalance(), HttpStatus.OK);
    }


}
