package com.db.awmd.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Account does not exist")
public class AccountNotFoundException extends RuntimeException {

  public AccountNotFoundException(String accountId) {
    super(MessageFormat.format("Account {0} does not exist.", accountId));
  }
}
