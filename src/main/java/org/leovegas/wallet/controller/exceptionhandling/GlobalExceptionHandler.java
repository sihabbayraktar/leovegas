package org.leovegas.wallet.controller.exceptionhandling;


import org.leovegas.wallet.exception.BalanceInsufficientException;
import org.leovegas.wallet.exception.NonUniqueTransactionException;
import org.leovegas.wallet.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler(value = NonUniqueTransactionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleTransactionException(Exception e, WebRequest request) {
        logger.error("Transaction is not unique", e);
        return createErrorResponse(e, e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = BalanceInsufficientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBalanceInsufficientException(Exception e, WebRequest request) {
        logger.error("Balance is insufficient", e);
        return createErrorResponse(e, e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleUserNotFoundException(Exception e, WebRequest request) {
        logger.error("Failed to find the User", e);
        return createErrorResponse(e, e.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception e, WebRequest request) {
        logger.error("Unknown error captured", e);
        return createErrorResponse(e, "Unknown error captured", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error. Check 'errors' field for details.");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        Collections.sort(errorResponse.getErrors());
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    private ResponseEntity<Object> createErrorResponse(Exception exception,
                                                       String message,
                                                       HttpStatus httpStatus,
                                                       WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

}
