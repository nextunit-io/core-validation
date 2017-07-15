package io.nextunit.validation.annotation;

import io.nextunit.validation.validator.SecretPasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {SecretPasswordValidator.class})
@Documented
public @interface SecretPassword {
    String message() default "";

    SecretPasswordConstraint[] constraints() default {
            @SecretPasswordConstraint(
                    message = "{validate.user.password.length}",
                    regularExpression = ".{8,}",
                    variables = @ValidationVariable(variable = "minLength", value = "8")),
            @SecretPasswordConstraint(
                    message = "{validate.user.password.number}",
                    regularExpression = SecretPasswordValidator.NUMBER_PATTERN),
            @SecretPasswordConstraint(
                    message = "{validate.user.password.char}",
                    regularExpression = SecretPasswordValidator.CHAR_PATTERN),
            @SecretPasswordConstraint(
                    message = "{validate.user.password.special_char}",
                    regularExpression = SecretPasswordValidator.SPECIAL_CHAR_PATTERN)
    };

    boolean nullable() default true;


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
