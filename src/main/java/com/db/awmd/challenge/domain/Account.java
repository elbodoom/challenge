package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.exception.InsufficientFundsException;
import com.db.awmd.challenge.validator.MinBalanceConstraint;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @MinBalanceConstraint
  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private final AtomicReference<BigDecimal> balance;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = new AtomicReference<>(BigDecimal.ZERO);
  }

  @JsonCreator
  public Account(
          @JsonProperty("accountId") String accountId,
          @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance == null ? null : new AtomicReference<>(balance);
  }

  public BigDecimal getBalance() {
    return balance == null ? null : balance.get();
  }

  /**
   * Withdraw a specified ammount of the current balance
   *
   * @param ammount the ammount to be withdrawn
   * @return the current balance after withdrawn
   * @throws NullPointerException       if the specified ammount is null
   * @throws IllegalArgumentException   if the specified ammount is a negative value
   * @throws InsufficientFundsException if the account has insufficient funds to be withdrawn
   */
  public BigDecimal withDraw(BigDecimal ammount) throws NullPointerException, IllegalArgumentException, InsufficientFundsException {
    checkSpecifiedAmmount(ammount);

    BigDecimal previousBalance;
    BigDecimal newBalance;
    //This loop is necessary to retry the withdraw if the balance has been changed by another thread
    do {
       previousBalance = this.balance.get();
       newBalance = previousBalance.subtract(ammount);

      if(newBalance.compareTo(BigDecimal.ZERO) < 0) {
        throw new InsufficientFundsException(previousBalance, ammount);
      }
    }while(!this.balance.compareAndSet(previousBalance, newBalance));
    return newBalance;
  }

  /**
   * Deposit an ammount into the current balance.
   *
   * @param ammount the ammount to be deposited into the balance
   * @return the current balance after the deposit
   * @throws NullPointerException       if the specified ammount is null
   * @throws IllegalArgumentException   if the specified ammount is a negative value
   */
  public BigDecimal deposit(BigDecimal ammount) throws NullPointerException, IllegalArgumentException {
    checkSpecifiedAmmount(ammount);
    return this.balance.updateAndGet(value -> value.add(ammount));
  }

  private void checkSpecifiedAmmount(BigDecimal ammount) throws NullPointerException, IllegalArgumentException {
    if (ammount == null) {
      throw new NullPointerException("It is not possible to transfer with a null ammount.");
    }
    if (ammount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(
              MessageFormat.format(
                      "It is not possible to transfer with a non positive ammount. Ammount: {0}.",
                      ammount)
      );
    }
  }
}
