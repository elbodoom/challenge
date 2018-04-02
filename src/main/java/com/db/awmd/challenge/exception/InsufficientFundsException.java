package com.db.awmd.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;
import java.text.MessageFormat;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Insufficient funds")
public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(BigDecimal balance, BigDecimal ammount) {
    super(MessageFormat.format(
            "The account has insufficient funds to be withdrawn. Balance: {0}, Ammount: {1}.",
            balance,
            ammount));
  }
}
