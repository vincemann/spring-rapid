package com.github.vincemann.springrapid.core.controller.dto.map;

import com.github.vincemann.springrapid.core.controller.dto.DtoValidationStrategy;
import com.github.vincemann.springrapid.core.util.ValidationUtil;

import javax.validation.*;
import java.util.Set;

/**
 * BaseImpl of {@link DtoValidationStrategy}, that utilizes the javax validation API.
 * See: {@link Validator}
 */
public class JavaXDtoValidationStrategy implements DtoValidationStrategy {
    private final Validator validator;

    public JavaXDtoValidationStrategy(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(Object dto) throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(dto);
        if(!constraintViolations.isEmpty())
            throw new ConstraintViolationException(constraintViolations);
    }

    @Override
    public void validatePartly(Object dto, Set<String> fields) {
        for (String field : fields) {
            Set<ConstraintViolation<Object>> violations = validator.validateProperty(dto, field);

            if (!violations.isEmpty()) {
                // Throw an exception with the violations
                throw new ConstraintViolationException(violations);
            }
        }
    }

}
