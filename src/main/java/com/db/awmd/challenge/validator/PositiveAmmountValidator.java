package com.db.awmd.challenge.validator;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * Implementation of the {@link PositiveAmmountConstraint}.
 */
@Component
class PositiveAmmountValidator implements ConstraintValidator<PositiveAmmountConstraint, BigDecimal> {

  @Override
  public void initialize(PositiveAmmountConstraint constraintAnnotation) {

  }

  @Override
  public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
    //It is not responsability of this constraint to validate if the value is null
    if(value == null) {
      return true;
    } else {
      return value.compareTo(BigDecimal.ZERO) > 0;
    }
  }
}
