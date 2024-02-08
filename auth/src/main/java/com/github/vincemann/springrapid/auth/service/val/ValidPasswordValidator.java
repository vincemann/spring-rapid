package com.github.vincemann.springrapid.auth.service.val;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// null is considered
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private PasswordValidator passwordValidator;

    @Autowired
    public ValidPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }


    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null)
            return false;
        try {
            passwordValidator.validate(password);
            return true; // Password is valid
        } catch (BadEntityException e) {
            context.disableDefaultConstraintViolation(); // Disable default message
            context.buildConstraintViolationWithTemplate(e.getMessage()) // Add custom message
                    .addConstraintViolation();
            return false; // Password is invalid
        }
    }
}
