package io.nextunit.validation.annotation;

public @interface SecretPasswordConstraint {
    String message();
    String regularExpression();
    ValidationVariable[] variables() default {};
}

