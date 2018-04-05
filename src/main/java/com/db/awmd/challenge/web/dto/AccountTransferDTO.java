package com.db.awmd.challenge.web.dto;

import com.db.awmd.challenge.validator.PositiveAmmountConstraint;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AccountTransferDTO {
  @NotNull
  private String accountFrom;
  @NotNull
  private String accountTo;
  @NotNull
  @PositiveAmmountConstraint
  private BigDecimal ammount;
}
