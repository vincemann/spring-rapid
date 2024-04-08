package com.github.vincemann.springrapid.auth.val;

import com.github.vincemann.springrapid.auth.BadEntityException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Email;
import java.util.Set;

/**
 * Enforces the {@link Email} constraint programmatically
 */
public class EmailContactInformationValidator implements ContactInformationValidator {

    private Validator validator;

    public EmailContactInformationValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public void validate(String contactInformation) throws BadEntityException {
        // Use a dummy object to apply the constraint
        DummyEmailContainer dummy = new DummyEmailContainer(contactInformation);
        Set<ConstraintViolation<DummyEmailContainer>> violations = validator.validate(dummy);
        if (!violations.isEmpty()) {
            // Aggregate the error messages or throw an exception
            throw new BadEntityException("Invalid email address.");
        }
    }

    // Dummy class to hold the email string for validation
    private static class DummyEmailContainer {
        @Email
        private String email;

        public DummyEmailContainer(String email) {
            this.email = email;
        }
    }
}