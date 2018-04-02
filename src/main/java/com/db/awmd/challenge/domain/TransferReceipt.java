package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransferReceipt {
  @JsonProperty("from")
  private final String accountFrom;
  @JsonProperty("to")
  private final String accountTo;
  private final BigDecimal ammount;

  private TransferReceipt(Builder builder) {
    this.accountFrom = builder.from.getAccountId();
    this.accountTo = builder.to.getAccountId();
    this.ammount = builder.ammount;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return "TransferReceipt{" +
            "accountFrom='" + accountFrom + '\'' +
            ", accountTo='" + accountTo + '\'' +
            ", ammount=" + ammount +
            '}';
  }

  public static class Builder {
    private Account from;
    private Account to;
    private BigDecimal ammount;

    public Builder from(Account from) {
      this.from = from;
      return this;
    }

    public Builder to(Account to) {
      this.to = to;
      return this;
    }

    public Builder ammount(BigDecimal ammount) {
      this.ammount = ammount;
      return this;
    }

    public TransferReceipt build() {
      return new TransferReceipt(this);
    }
  }
}
