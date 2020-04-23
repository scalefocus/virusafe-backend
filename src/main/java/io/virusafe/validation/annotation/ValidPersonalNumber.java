package io.virusafe.validation.annotation;

import io.virusafe.validation.personalnumber.PersonalNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PersonalNumberValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPersonalNumber {

    String message() default "Invalid personal number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
