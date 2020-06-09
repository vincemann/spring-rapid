package com.github.vincemann.springlemon.auth.domain.dto;

import com.github.vincemann.springlemon.auth.validation.Password;
import com.github.vincemann.springlemon.auth.validation.RetypePassword;
import com.github.vincemann.springlemon.auth.validation.RetypePasswordForm;

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
