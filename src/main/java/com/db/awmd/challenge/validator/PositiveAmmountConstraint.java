package com.db.awmd.challenge.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint to validate if the ammount is a positive value
 */
@Documented
@Constraint(validatedBy = PositiveAmmountValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveAmmountConstraint {
  String message() default "The ammount should be a positive value";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
