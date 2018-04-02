package com.db.awmd.challenge.validator;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of the {@link MinBalanceConstraint}. This implementation is necessary because
 * the {@link com.db.awmd.challenge.domain.Account#balance} is wrapped into {@link AtomicReference} and
 * the @{@link javax.validation.constraints.Min} annotation does not work on this kind of object.
 */
@Component
class MinBalanceValidator implements ConstraintValidator<MinBalanceConstraint, AtomicReference<BigDecimal>> {

  @Override
  public void initialize(MinBalanceConstraint constraintAnnotation) {

  }

  @Override
  public boolean isValid(AtomicReference<BigDecimal> value, ConstraintValidatorContext context) {
    //It is not responsability of this constraint to validate if the value is null
    if(value == null || value.get() == null) {
      return true;
    } else {
      return value.get().compareTo(BigDecimal.ZERO) >= 0;
    }
  }
}
