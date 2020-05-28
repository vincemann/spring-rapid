package io.github.vincemann.springlemon.auth.domain.dto;

import io.github.vincemann.springlemon.auth.validation.Password;
import io.github.vincemann.springlemon.auth.validation.RetypePassword;
import io.github.vincemann.springlemon.auth.validation.RetypePasswordForm;

import lombok.*;

/**
 * Change password form.
 * 
 * @author Sanjay Patel
 */
@RetypePassword
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ChangePasswordForm implements RetypePasswordForm {
	
	@Password
	private String oldPassword;

	@Password
	private String password;
	
	@Password
	private String retypePassword;
}
