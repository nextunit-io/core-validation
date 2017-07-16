package io.nextunit.core.validation.validator;

import io.nextunit.core.validation.annotation.SecretPasswordConstraint;
import io.nextunit.core.validation.annotation.ValidationVariable;
import io.nextunit.core.validation.annotation.SecretPassword;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.validation.ConstraintValidatorContext;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

@SuppressWarnings("ConstantConditions")
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest(SpringBeanAutowiringSupport.class)
public class SecretPasswordValidatorTest {
    private SecretPasswordValidator secretPasswordValidator;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SecretPassword secretPasswordMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConstraintValidatorContext constraintValidatorContextMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HibernateConstraintValidatorContext hibernateConstraintValidatorContextMock;

    @Mock
    private SecretPasswordConstraint secretPasswordConstraintMock;

    @Mock
    private ValidationVariable validationVariableMock;

    private String validPassword = "IsValidPassword12";
    private String variableName = "testVariableName";
    private String variableValue = "testVariableValue";
    private String constraintMessage = "testMessage";

    @Before
    public void setUp() throws Exception {
        secretPasswordValidator = new SecretPasswordValidator();
        PowerMockito.mockStatic(SpringBeanAutowiringSupport.class);
    }

    @Test
    public void initialize() throws Exception {
        // GIVEN
        PowerMockito.doNothing().when(SpringBeanAutowiringSupport.class,
                "processInjectionBasedOnCurrentContext", any());

        // WHEN
        secretPasswordValidator.initialize(secretPasswordMock);

        // THEN
        PowerMockito.verifyStatic(times(1));
    }

    @Test
    public void isValidValidPassword() throws Exception {
        // GIVEN
        PowerMockito.doNothing().when(SpringBeanAutowiringSupport.class,
                "processInjectionBasedOnCurrentContext", any());
        secretPasswordValidator.initialize(secretPasswordMock);
        Mockito.when(secretPasswordMock.constraints()).thenReturn
                (new SecretPasswordConstraint[]{
                        secretPasswordConstraintMock,
                        secretPasswordConstraintMock
                });
        Mockito.when(secretPasswordConstraintMock.regularExpression())
                // First Call
                .thenReturn(".{8,}")
                // Second Call
                .thenReturn(SecretPasswordValidator.NUMBER_PATTERN);

        // WHEN
        boolean valid = secretPasswordValidator.isValid(
                validPassword,
                constraintValidatorContextMock
        );

        // THEN
        Assert.assertTrue(valid);
    }

    @Test
    public void isValidValidPasswordBecauseNullPassword() throws Exception {
        // GIVEN
        PowerMockito.doNothing().when(SpringBeanAutowiringSupport.class,
                "processInjectionBasedOnCurrentContext", any());
        secretPasswordValidator.initialize(secretPasswordMock);
        Mockito.when(secretPasswordMock.nullable()).thenReturn(true);

        // WHEN
        boolean valid = secretPasswordValidator.isValid(
                null,
                constraintValidatorContextMock
        );

        // THEN
        Assert.assertTrue(valid);
    }

    @Test
    public void isValidInvalidPasswordBecauseNullPassword() throws Exception {
        // GIVEN
        PowerMockito.doNothing().when(SpringBeanAutowiringSupport.class,
                "processInjectionBasedOnCurrentContext", any());
        secretPasswordValidator.initialize(secretPasswordMock);
        Mockito.when(secretPasswordMock.nullable()).thenReturn(false);

        // WHEN
        boolean valid = secretPasswordValidator.isValid(
                null,
                constraintValidatorContextMock
        );

        // THEN
        Assert.assertFalse(valid);
    }

    @Test
    public void isValidInvalidPasswordBecauseEmptyPassword() throws Exception {
        // GIVEN
        PowerMockito.doNothing().when(SpringBeanAutowiringSupport.class,
                "processInjectionBasedOnCurrentContext", any());
        secretPasswordValidator.initialize(secretPasswordMock);
        Mockito.when(secretPasswordMock.nullable()).thenReturn(false);

        // WHEN
        boolean valid = secretPasswordValidator.isValid(
                "",
                constraintValidatorContextMock
        );

        // THEN
        Assert.assertFalse(valid);
    }

    @Test
    public void isValidInvalidPasswordNoMatch() throws Exception {
        // GIVEN
        PowerMockito.doNothing().when(SpringBeanAutowiringSupport.class,
                "processInjectionBasedOnCurrentContext", any());
        secretPasswordValidator.initialize(secretPasswordMock);
        Mockito.when(secretPasswordMock.constraints()).thenReturn
                (new SecretPasswordConstraint[]{
                        secretPasswordConstraintMock
                });
        Mockito.when(secretPasswordConstraintMock.regularExpression())
                // First Call
                .thenReturn(".{8,}");
        Mockito.when(secretPasswordConstraintMock.variables()).thenReturn(
                new ValidationVariable[]{
                        validationVariableMock
                }
        );
        Mockito.when(validationVariableMock.variable()).thenReturn(variableName);
        Mockito.when(validationVariableMock.value()).thenReturn(variableValue);
        Mockito.when(secretPasswordConstraintMock.message()).thenReturn(constraintMessage);
        Mockito.when(constraintValidatorContextMock
                .unwrap(HibernateConstraintValidatorContext.class))
                .thenReturn(hibernateConstraintValidatorContextMock);

        // WHEN
        boolean valid = secretPasswordValidator.isValid(
                "invalid",
                constraintValidatorContextMock
        );

        // THEN
        Mockito.verify(hibernateConstraintValidatorContextMock, times(1))
                .addExpressionVariable(variableName, variableValue);
        Mockito.verify(hibernateConstraintValidatorContextMock, times(1))
                .disableDefaultConstraintViolation();
        Mockito.verify(hibernateConstraintValidatorContextMock
                        .buildConstraintViolationWithTemplate(constraintMessage),times(1))
                .addConstraintViolation();
        Assert.assertFalse(valid);
    }
}
