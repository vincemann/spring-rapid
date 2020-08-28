package com.github.vincemann.springlemon.auth.validation;

import com.github.vincemann.springlemon.auth.util.UserVerifyUtils;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for password constraint
 * 
 * @see <a href="http://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#example-composed-constraint">Composed constraint example</a>
 *  
 * @author Sanjay Patel
 *
 */
@NotBlank(message="{com.naturalprogrammer.spring.blank.password}")
@Size(min= UserVerifyUtils.PASSWORD_MIN, max= UserVerifyUtils.PASSWORD_MAX,
	message="{com.naturalprogrammer.spring.invalid.password.size}")
@Retention(RUNTIME)
@Constraint(validatedBy = { })
public @interface Password {
	
	String message() default "{com.naturalprogrammer.spring.invalid.password.size}";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
