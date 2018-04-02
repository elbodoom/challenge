package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.exception.InsufficientFundsException;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AccountTest {

  @Test
  public void deposit() throws Exception {
    Account account = new Account("1");
    assertEquals(BigDecimal.ONE, account.deposit(new BigDecimal(1L)));
    assertEquals(BigDecimal.ONE, account.getBalance());
  }

  @Test(expected = NullPointerException.class)
  public void deposit_NullAmmount() {
    Account account = new Account("1");
    account.deposit(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void deposit_NegativeAmmount() {
    Account account = new Account("1");
    account.deposit(new BigDecimal(-1));
  }

  @Test
  public void withdraw() throws Exception {
    Account account = new Account("1", BigDecimal.ONE);
    assertEquals(BigDecimal.ZERO, account.withDraw(BigDecimal.ONE));
    assertEquals(BigDecimal.ZERO, account.getBalance());
  }

  @Test(expected = NullPointerException.class)
  public void withdraw_NullAmmount() throws Exception {
    Account account = new Account("1");
    account.withDraw(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void withdraw_NegativeAmmount() throws Exception {
    Account account = new Account("1");
    account.withDraw(new BigDecimal(-1));
  }

  @Test(expected = InsufficientFundsException.class)
  public void withdraw_InsufficientFunds() throws Exception {
    Account account = new Account("1", BigDecimal.ONE);
    account.withDraw(BigDecimal.TEN);
  }

  @Test
  public void withDraw_concurrent() throws Exception {
    Account account = new Account("1", BigDecimal.TEN);

    Thread t1 = new Thread(() -> account.withDraw(BigDecimal.valueOf(2)));
    Thread t2 = new Thread(() -> account.withDraw(BigDecimal.valueOf(8)));
    t1.setName("Thread 1");
    t2.setName("Thread 2");
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    assertEquals(BigDecimal.ZERO, account.getBalance());
  }

  @Test
  public void deposit_concurrent() throws Exception {
    Account account = new Account("1", BigDecimal.ZERO);

    Thread t1 = new Thread(() -> account.deposit(BigDecimal.valueOf(2)));
    Thread t2 = new Thread(() -> account.deposit(BigDecimal.valueOf(8)));
    t1.setName("Thread 1");
    t2.setName("Thread 2");
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    assertEquals(BigDecimal.TEN, account.getBalance());
  }
}
