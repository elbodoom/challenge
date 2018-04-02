package com.db.awmd.challenge.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Constraint to validate if balance is positive.
 */
@Documented
@Constraint(validatedBy = MinBalanceValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MinBalanceConstraint {
  String message() default "Initial balance must be positive";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
