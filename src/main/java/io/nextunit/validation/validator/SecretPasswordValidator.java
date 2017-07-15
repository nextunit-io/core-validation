package io.nextunit.validation.validator;

import io.nextunit.validation.annotation.SecretPassword;
import io.nextunit.validation.annotation.SecretPasswordConstraint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * This validator provides rules for a secret password.
 */
public class SecretPasswordValidator implements ConstraintValidator<SecretPassword, String> {
    public static final String NUMBER_PATTERN = ".*\\d.*";
    public static final String CHAR_PATTERN = ".*[A-z].*";
    public static final String SPECIAL_CHAR_PATTERN = ".*[\\!|\\@|\\#|\\$|\\%|\\^|\\&|\\*|\\-].*";

    private final Log logger = LogFactory.getLog(getClass());

    private SecretPassword currentAnnotation;

    /**
     * Initialize calls the spring bean support.
     *
     * @param secretPassword {@link SecretPassword} annotation.
     */
    @Override
    public void initialize(SecretPassword secretPassword) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        currentAnnotation = secretPassword;
    }

    /**
     * This checks if the password satisfies the defined rules.
     *
     * @param password                   Password which should be checked.
     * @param constraintValidatorContext Context
     *
     * @return boolean true, if the password matches our rules, false if not.
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (currentAnnotation.nullable() && password == null) {
            return true;
        }

        if (!currentAnnotation.nullable() && (password == null || password.isEmpty())) {
            return false;
        }

        SecretPasswordConstraint[] constraints = currentAnnotation.constraints();

        for (SecretPasswordConstraint constraint : constraints) {
            if (!password.matches(constraint.regularExpression())) {
                logger.debug(String.format("[ERROR] Constraint violation - Regular expression: '%s'.",
                        constraint.regularExpression()));

                HibernateConstraintValidatorContext context = constraintValidatorContext.unwrap(
                        HibernateConstraintValidatorContext.class);

                Arrays.stream(constraint.variables())
                        .forEach(variable -> context.addExpressionVariable(variable.variable(), variable.value()));

                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(constraint.message())
                        .addConstraintViolation();

                return false;
            }

            logger.debug(String.format("[OK] Constraint violation - Regular expression: '%s'.",
                    constraint.regularExpression()));
        }

        return true;
    }
}
