package com.github.vincemann.springrapid.auth.validation;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.auth.domain.AbstractUserRepository;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validator for unique-email
 * 
 * @author Sanjay Patel
 */
@ServiceComponent
@Slf4j
public class UniqueEmailValidator
implements ConstraintValidator<UniqueEmail, String>, AopLoggable {


	private AbstractUserRepository<?,?> userRepository;

	public UniqueEmailValidator(AbstractUserRepository<?, ?> userRepository) {
		
		this.userRepository = userRepository;

	}

	@LogInteraction
	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		
		log.debug("Validating whether email is unique: " + email);
		return !userRepository.findByEmail(email).isPresent();
	}
}
