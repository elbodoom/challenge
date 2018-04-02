package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferReceipt;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.InsufficientFundsException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  @Getter
  private final NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public TransferReceipt transfer(String accountFrom, String accountTo, BigDecimal ammount) throws AccountNotFoundException, InsufficientFundsException {
    Account from = getOrRaiseNotFoundException(accountFrom);
    Account to = getOrRaiseNotFoundException(accountTo);

    TransferReceipt receipt = executeTransfer(ammount, from, to);
    notify(from, to, receipt);

    return receipt;
  }

  private Account getOrRaiseNotFoundException(String accoundId) throws AccountNotFoundException {
    Optional<Account> account = Optional.ofNullable(this.accountsRepository.getAccount(accoundId));
    return account.orElseThrow(() -> new AccountNotFoundException(accoundId));
  }

  private TransferReceipt executeTransfer(BigDecimal ammount, Account from, Account to) {
    from.withDraw(ammount);
    to.deposit(ammount);

    return generateReceipt(ammount, from, to);
  }

  private TransferReceipt generateReceipt(BigDecimal ammount, Account from, Account to) {
    return TransferReceipt.builder()
            .from(from)
            .to(to)
            .ammount(ammount)
            .build();
  }

  private void notify(Account from, Account to, TransferReceipt receipt) {
    notificationService.notifyAboutTransfer(from, receipt.toString());
    notificationService.notifyAboutTransfer(to, receipt.toString());
  }
}
