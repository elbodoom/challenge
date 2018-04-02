package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientFundsException;
import com.db.awmd.challenge.service.AccountsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Before
  public void clearAccounts() {
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  @Test
  public void transfer() {
    accountsService.createAccount(new Account("1", BigDecimal.ONE));
    accountsService.createAccount(new Account("2", BigDecimal.ONE));
    accountsService.transfer("1", "2", BigDecimal.ONE);
    assertEquals("from's balance should be 0 after first transfer", BigDecimal.valueOf(0), accountsService.getAccount("1").getBalance());
    assertEquals("to's balance should be 2 after first transfer", BigDecimal.valueOf(2), accountsService.getAccount("2").getBalance());
    accountsService.transfer("2", "1", BigDecimal.ONE);
    assertEquals("from's balance should be 1 after second transfer", BigDecimal.valueOf(1), accountsService.getAccount("1").getBalance());
    assertEquals("to's balance should be 1 after second transfer", BigDecimal.valueOf(1), accountsService.getAccount("2").getBalance());
  }

  @Test(expected = AccountNotFoundException.class)
  public void transfer_fromDoesNotExist() {
    accountsService.createAccount(new Account("1", BigDecimal.ONE));
    accountsService.createAccount(new Account("2", BigDecimal.ONE));
    accountsService.transfer("999", "2", BigDecimal.ONE);
  }

  @Test(expected = AccountNotFoundException.class)
  public void transfer_toDoesNotExist() {
    accountsService.createAccount(new Account("1", BigDecimal.ONE));
    accountsService.createAccount(new Account("2", BigDecimal.ONE));
    accountsService.transfer("1", "999", BigDecimal.ONE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void transfer_invalidAmmount_Zero() {
    accountsService.createAccount(new Account("1", BigDecimal.ONE));
    accountsService.createAccount(new Account("2", BigDecimal.ONE));
    accountsService.transfer("1", "2", BigDecimal.ZERO);
  }

  @Test(expected = IllegalArgumentException.class)
  public void transfer_invalidAmmount_Negative() {
    accountsService.createAccount(new Account("1", BigDecimal.ONE));
    accountsService.createAccount(new Account("2", BigDecimal.ONE));
    accountsService.transfer("1", "2", BigDecimal.valueOf(-1));
  }

  @Test(expected = InsufficientFundsException.class)
  public void transfer_insufficientFunds() {
    accountsService.createAccount(new Account("1", BigDecimal.ONE));
    accountsService.createAccount(new Account("2", BigDecimal.ONE));
    accountsService.transfer("1", "2", BigDecimal.TEN);
  }
}
