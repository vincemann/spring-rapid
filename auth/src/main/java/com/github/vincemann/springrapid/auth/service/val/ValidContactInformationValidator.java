package com.github.vincemann.springrapid.auth.service.val;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidContactInformationValidator implements ConstraintValidator<ValidContactInformation, String> {

    private ContactInformationValidator validator;

    @Autowired
    public ValidContactInformationValidator(ContactInformationValidator validator) {
        this.validator = validator;
    }


    @Override
    public void initialize(ValidContactInformation constraintAnnotation) {
    }

    @Override
    public boolean isValid(String contactInformation, ConstraintValidatorContext context) {
        if (contactInformation == null)
            return false;
        try {
            validator.validate(contactInformation);
            return true; // ci is valid
        } catch (BadEntityException e) {
            context.disableDefaultConstraintViolation(); // Disable default message
            context.buildConstraintViolationWithTemplate(e.getMessage()) // Add custom message
                    .addConstraintViolation();
            return false; // ci is invalid
        }
    }
}